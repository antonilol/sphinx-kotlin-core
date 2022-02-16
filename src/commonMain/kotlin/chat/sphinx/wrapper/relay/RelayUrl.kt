package chat.sphinx.wrapper.relay

import kotlin.jvm.JvmInline

@Suppress("NOTHING_TO_INLINE")
inline fun String.toRelayUrl(): RelayUrl? =
    try {
        RelayUrl(this)
    } catch (e: IllegalArgumentException) {
        null
    }

inline val String.isOnionAddress: Boolean
    get() = this
        .replaceFirst("http://", "")
        .replaceFirst("https://", "")
        .matches("([a-z2-7]{56}).onion.*".toRegex())

inline val String.isValidRelayUrl: Boolean
    get() = toRelayUrl() != null

inline val RelayUrl.isOnionAddress: Boolean
    get() = value.isOnionAddress

@JvmInline
value class RelayUrl(val value: String){
    init {
        require(value.isNotEmpty()) {
            "RelayUrl cannot be empty"
        }
    }
}
