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
package chat.sphinx.concepts.authentication.data

import kotlin.coroutines.cancellation.CancellationException

interface AuthenticationStorage {

    companion object {
        const val CREDENTIALS = "CREDENTIALS"
    }

    suspend fun getString(key: String, defaultValue: String?): String?

    /**
     * If the [key] is [CREDENTIALS], an [IllegalArgumentException] is thrown. This inhibits
     * overwriting of the key value pair used by the AuthenticationManager.
     * */
    @Throws(IllegalArgumentException::class, CancellationException::class)
    suspend fun putString(key: String, value: String?)

    /**
     * If the [key] is [CREDENTIALS], an [IllegalArgumentException] is thrown.
     * */
    @Throws(IllegalArgumentException::class, CancellationException::class)
    suspend fun removeString(key: String)
}