package chat.sphinx.wrapper.message.media

import kotlin.jvm.JvmInline

@Suppress("NOTHING_TO_INLINE")
inline fun String.toMediaKeyDecrypted(): MediaKeyDecrypted? =
    try {
        MediaKeyDecrypted(this)
    } catch (e: IllegalArgumentException) {
        null
    }

@JvmInline
value class MediaKeyDecrypted(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "MediaKeyDecrypted cannot be empty"
        }
    }
}

