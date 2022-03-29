/*
*  Copyright 2021 Matthew Nelson
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
* */
package chat.sphinx.features.authentication.core

import chat.sphinx.concepts.authentication.coordinator.AuthenticationRequest
import chat.sphinx.concepts.authentication.coordinator.AuthenticationResponse
import chat.sphinx.concepts.authentication.core.AuthenticationManager
import chat.sphinx.concepts.authentication.core.model.UserInput
import chat.sphinx.concepts.authentication.encryption_key.EncryptionKey
import chat.sphinx.concepts.authentication.encryption_key.EncryptionKeyException
import chat.sphinx.concepts.authentication.encryption_key.EncryptionKeyHandler
import chat.sphinx.concepts.authentication.state.AuthenticationState
import chat.sphinx.concepts.coroutines.CoroutineDispatchers
import chat.sphinx.crypto.common.annotations.RawPasswordAccess
import chat.sphinx.crypto.common.clazzes.HashIterations
import chat.sphinx.crypto.common.clazzes.Password
import chat.sphinx.crypto.common.clazzes.clear
import chat.sphinx.crypto.common.clazzes.compare
import chat.sphinx.features.authentication.core.components.AuthenticationManagerInitializer
import chat.sphinx.features.authentication.core.components.AuthenticationProcessor
import chat.sphinx.features.authentication.core.data.AuthenticationCoreStorage
import chat.sphinx.features.authentication.core.model.AuthenticateFlowResponse
import chat.sphinx.features.authentication.core.model.UserInputWriter
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized
import kotlinx.coroutines.flow.*
import kotlin.jvm.JvmSynthetic
import kotlin.jvm.Synchronized
import kotlin.jvm.Volatile

/**
 * Extend this class and implement
 * */
abstract class AuthenticationCoreManager(
    dispatchers: CoroutineDispatchers,
    encryptionKeyHashIterations: HashIterations,
    encryptionKeyHandler: EncryptionKeyHandler,
    authenticationCoreStorage: AuthenticationCoreStorage,
    authenticationManagerInitializer: AuthenticationManagerInitializer
): AuthenticationManager<
        AuthenticateFlowResponse,
        AuthenticateFlowResponse.PasswordConfirmedForReset,
        AuthenticateFlowResponse.ConfirmInputToSetForFirstTime
        >()
{

    ///////////////////////////
    /// AuthenticationState ///
    ///////////////////////////
    private val _authenticationStateFlow: MutableStateFlow<AuthenticationState> by lazy {
        MutableStateFlow(AuthenticationState.Required.InitialLogIn)
    }

    override val authenticationStateFlow: StateFlow<AuthenticationState>
        get() = _authenticationStateFlow.asStateFlow()

    @JvmSynthetic
    @Suppress("UNUSED_PARAMETER")
    @Throws(IllegalArgumentException::class)
    internal fun updateAuthenticationState(
        state: AuthenticationState,
        encryptionKey: EncryptionKey?
    ) {
        Exhaustive@
        when (state) {
            is AuthenticationState.NotRequired -> {
                encryptionKey?.let { nnKey ->
                    setEncryptionKey(nnKey)

                    if (_authenticationStateFlow.value == AuthenticationState.Required.InitialLogIn) {
                        onInitialLoginSuccess(nnKey)
                    }

                } ?: throw IllegalArgumentException(
                    "An EncryptionKey is required when setting AuthenticationState to NotRequired "
                )
            }
            is AuthenticationState.Required -> {
                setEncryptionKey(null)
            }
        }
        _authenticationStateFlow.value = state
    }

    /**
     * Called when [AuthenticationState] transitions from [AuthenticationState.Required.InitialLogIn]
     * to [AuthenticationState.NotRequired]
     * */
    protected open fun onInitialLoginSuccess(encryptionKey: EncryptionKey) {}

    protected fun setAuthenticationStateRequired(state: AuthenticationState.Required) {
        updateAuthenticationState(state, null)
    }

    //////////////////////
    /// Initialization ///
    //////////////////////
    init {
        minUserInputLength = authenticationManagerInitializer.minimumUserInputLength
        maxUserInputLength = authenticationManagerInitializer.maximumUserInputLength
    }

    companion object {
        var minUserInputLength: Int = 8
            private set
        var maxUserInputLength: Int = 42
            private set
    }

    //////////////////////
    /// Authentication ///
    //////////////////////
    @Suppress("RemoveExplicitTypeArguments")
    private val authenticationProcessor: AuthenticationProcessor by lazy {
        AuthenticationProcessor.instantiate(
            this,
            dispatchers,
            encryptionKeyHashIterations,
            encryptionKeyHandler,
            authenticationCoreStorage,
            authenticationManagerInitializer
        )
    }

    override suspend fun isAnEncryptionKeySet(): Boolean {
        return authenticationProcessor.isAnEncryptionKeySet()
    }

    override fun getNewUserInput(): UserInput {
        return UserInputWriter.instantiate()
    }

    @Synchronized
    @OptIn(RawPasswordAccess::class)
    override fun authenticate(
        privateKey: Password,
        request: AuthenticationRequest.LogIn
    ): Flow<AuthenticationResponse> =
        getEncryptionKey()?.let { alreadySetKey ->

            if (!alreadySetKey.privateKey.compare(privateKey)) {
                return flowOf(
                    AuthenticationResponse.Failure(request)
                )
            }

            if (authenticationStateFlow.value is AuthenticationState.NotRequired) {
                return flowOf(
                    AuthenticationResponse.Success.Key(request, alreadySetKey)
                )
            }

            authenticationProcessor.authenticate(privateKey, request)
        } ?: authenticationProcessor.authenticate(privateKey, request)

    @Synchronized
    override fun authenticate(
        userInput: UserInput,
        requests: List<AuthenticationRequest>
    ): Flow<AuthenticateFlowResponse> =
        when {
            requests.isEmpty() -> {
                flowOf(AuthenticateFlowResponse.Error.RequestListEmpty)
            }
            try {
                (userInput as UserInputWriter).size() < minUserInputLength ||
                userInput.size() > maxUserInputLength
            } catch(e: ClassCastException) {
                // TODO: create new error to submit back instead of InvalidPasswordEntrySize
                true
            } -> {
                flowOf(AuthenticateFlowResponse.Error.Authenticate.InvalidPasswordEntrySize)
            }
            else -> {
                authenticationProcessor.authenticate(userInput as UserInputWriter, requests)
            }
        }

    @Synchronized
    override fun resetPassword(
        resetPasswordResponse: AuthenticateFlowResponse.PasswordConfirmedForReset,
        userInputConfirmation: UserInput,
        requests: List<AuthenticationRequest>
    ): Flow<AuthenticateFlowResponse> =
        when {
            requests.isEmpty() -> {
                flowOf(AuthenticateFlowResponse.Error.RequestListEmpty)
            }
            resetPasswordResponse.getNewPasswordToBeSet() == null -> {
                flowOf(AuthenticateFlowResponse.Error.ResetPassword.NewPasswordEntryWasNull)
            }

            resetPasswordResponse.getNewPasswordToBeSet()?.size() ?: 0
                    < minUserInputLength ||
            resetPasswordResponse.getNewPasswordToBeSet()?.size() ?: maxUserInputLength + 1
                    > maxUserInputLength -> {
                        flowOf(AuthenticateFlowResponse.Error.ResetPassword.InvalidNewPasswordEntrySize)
                    }

            try {
                (userInputConfirmation as UserInputWriter).size() < minUserInputLength ||
                        userInputConfirmation.size() > maxUserInputLength
            } catch (e: ClassCastException) {
                // TODO: create new error to submit back instead of InvalidPasswordEntrySize
                true
            } -> {
                flowOf(AuthenticateFlowResponse.Error.ResetPassword.InvalidConfirmedPasswordEntrySize)
            }
            resetPasswordResponse.originalValidatedUserInputHasBeenCleared -> {
                flowOf(AuthenticateFlowResponse.Error.ResetPassword.CurrentPasswordEntryWasCleared)
            }
            resetPasswordResponse.newPasswordHasBeenCleared -> {
                flowOf(AuthenticateFlowResponse.Error.ResetPassword.NewPasswordEntryWasCleared)
            }
            resetPasswordResponse.compareNewPasswordWithConfirmationInput(userInputConfirmation) != true -> {
                flowOf(AuthenticateFlowResponse.Error.ResetPassword.NewPinDoesNotMatchConfirmedPassword)
            }
            else -> {
                authenticationProcessor.resetPassword(resetPasswordResponse, requests)
            }
        }

    @Synchronized
    override fun setPasswordFirstTime(
        setPasswordFirstTimeResponse: AuthenticateFlowResponse.ConfirmInputToSetForFirstTime,
        userInputConfirmation: UserInput,
        requests: List<AuthenticationRequest>
    ): Flow<AuthenticateFlowResponse> =
        when {
            requests.isEmpty() -> {
                flowOf(AuthenticateFlowResponse.Error.RequestListEmpty)
            }
            try {
                (userInputConfirmation as UserInputWriter).size() < minUserInputLength ||
                        userInputConfirmation.size() > maxUserInputLength
            } catch (e: ClassCastException) {
                // TODO: create new error to submit back instead of InvalidPasswordEntrySize
                true
            } -> {
                flowOf(AuthenticateFlowResponse.Error.SetPasswordFirstTime.InvalidNewPasswordEntrySize)
            }
            !setPasswordFirstTimeResponse.compareInitialInputWithConfirmedInput(userInputConfirmation) -> {
                flowOf(AuthenticateFlowResponse.Error.SetPasswordFirstTime.NewPasswordDoesNotMatchConfirmedPassword)
            }
            setPasswordFirstTimeResponse.initialUserInputHasBeenCleared -> {
                flowOf(AuthenticateFlowResponse.Error.SetPasswordFirstTime.NewPasswordEntryWasCleared)
            }
            else -> {
                authenticationProcessor.setPasswordFirstTime(setPasswordFirstTimeResponse, requests)
            }
        }

    //////////////////////
    /// Encryption Key ///
    //////////////////////
    @Volatile
    private var encryptionKey: EncryptionKey? = null
    private val encryptionKeyLock = SynchronizedObject()

    fun getEncryptionKey(): EncryptionKey? =
        synchronized(encryptionKeyLock) {
            encryptionKey
        }

    @JvmSynthetic
    internal fun getEncryptionKeyCopy(): EncryptionKey? =
        synchronized(encryptionKeyLock) {
            encryptionKey?.let { key ->
                try {
                    @OptIn(RawPasswordAccess::class)
                    authenticationProcessor.encryptionKeyHandler
                        .storeCopyOfEncryptionKey(key.privateKey.value, key.publicKey.value)
                } catch (e: EncryptionKeyException) {
                    null
                }
            }
        }

    private fun setEncryptionKey(encryptionKey: EncryptionKey?) {
        synchronized(encryptionKeyLock) {

            // clear key if passed value is null
            if (encryptionKey == null) {
                this.encryptionKey?.privateKey?.clear()
                this.encryptionKey?.publicKey?.clear()
                this.encryptionKey = null
                return
            }

            this.encryptionKey?.let { currentKey ->
                // if current key is not null, compare with the passed key
                if (
                    !currentKey.privateKey.compare(encryptionKey.privateKey) ||
                    !currentKey.publicKey.compare(encryptionKey.publicKey)
                ) {

                    // if provided key was not the same, clear the current key and set
                    currentKey.privateKey.clear()
                    currentKey.publicKey.clear()
                    this.encryptionKey = encryptionKey
                }

                // otherwise do nothing

            } ?: let {

                // if no encryption key is set, set it to the provided key
                this.encryptionKey = encryptionKey

            }
        }
    }
}
