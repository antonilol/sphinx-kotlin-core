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
package chat.sphinx.crypto.common.clazzes

import com.soywiz.krypto.encoding.hex
import com.soywiz.krypto.sha256
import kotlinx.io.core.toByteArray
import kotlin.jvm.JvmInline

@Suppress("NOTHING_TO_INLINE")
inline fun ByteArray.toSha256Hash(): Sha256Hash {
    return Sha256Hash(this.sha256().hex)
}

@Suppress("NOTHING_TO_INLINE")
inline fun String.toSha256Hash(): Sha256Hash =
    toByteArray().toSha256Hash()

@JvmInline
value class Sha256Hash(val value: String) {

    init {
        require(isValid(value)) {
            "$value is not a valid Sha256 hash"
        }
    }

    companion object {
        fun isValid(sha256Hash: String): Boolean =
            sha256Hash.matches("[a-f0-9]{64}".toRegex())

        fun isValid(sha256Hash: ByteArray): Boolean =
            isValid(sha256Hash.hex)
    }
}