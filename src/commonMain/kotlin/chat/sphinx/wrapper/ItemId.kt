package chat.sphinx.wrapper

import kotlin.jvm.JvmInline

@Suppress("NOTHING_TO_INLINE")
inline fun Long.toItemId(): ItemId? =
    try {
        ItemId(this)
    } catch (e: IllegalArgumentException) {
        null
    }

@JvmInline
value class ItemId(val value: Long) {
    init {
        require(value >= -1) {
            "MetaDataId must be greater than or equal to -1"
        }
    }
}
