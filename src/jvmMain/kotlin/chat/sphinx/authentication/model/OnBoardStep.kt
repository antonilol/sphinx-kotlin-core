package chat.sphinx.authentication.model

import chat.sphinx.wrapper.relay.AuthorizationToken
import chat.sphinx.wrapper.relay.RelayUrl


@Suppress("DataClassPrivateConstructor", "ClassName")
sealed class OnBoardStep {

    data class Step1_WelcomeMessage private constructor(
        val relayUrl: RelayUrl,
        val authorizationToken: AuthorizationToken,
        val inviterData: OnBoardInviterData
    ): OnBoardStep() {

        companion object {
            @JvmSynthetic
            internal operator fun invoke(
                relayUrl: RelayUrl,
                authorizationToken: AuthorizationToken,
                inviterData: OnBoardInviterData
            ) : Step1_WelcomeMessage =
                Step1_WelcomeMessage(relayUrl, authorizationToken, inviterData)
        }

    }

    data class Step2_Name private constructor(val inviterData: OnBoardInviterData): OnBoardStep() {

        companion object {
            @JvmSynthetic
            internal operator fun invoke(inviterData: OnBoardInviterData): Step2_Name =
                Step2_Name(inviterData)
        }
    }

    data class Step3_Picture private constructor(val inviterData: OnBoardInviterData): OnBoardStep() {

        companion object {
            @JvmSynthetic
            internal operator fun invoke(inviterData: OnBoardInviterData): Step3_Picture =
                Step3_Picture(inviterData)
        }
    }

    data class Step4_Ready private constructor(val inviterData: OnBoardInviterData): OnBoardStep() {

        companion object {
            @JvmSynthetic
            internal operator fun invoke(inviterData: OnBoardInviterData): Step4_Ready =
                Step4_Ready(inviterData)
        }
    }

}
