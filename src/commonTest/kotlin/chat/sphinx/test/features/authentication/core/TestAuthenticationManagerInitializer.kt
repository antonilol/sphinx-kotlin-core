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
package chat.sphinx.test.features.authentication.core

import chat.sphinx.features.authentication.core.components.AuthenticationManagerInitializer


/**
 * Extend and implement your own overrides if desired.
 * */
open class TestAuthenticationManagerInitializer(
    minimumUserInputLength: Int = 8,
    maximumUserInputLength: Int = 42,
    wrongPinAttemptsUntilLockedOut: Int = 0,
    wrongPinLockoutDuration: Long = 0L,
): AuthenticationManagerInitializer(
    minimumUserInputLength,
    maximumUserInputLength,
    wrongPinAttemptsUntilLockedOut,
    wrongPinLockoutDuration
)