package chat.sphinx.wrapper.feed

import kotlin.jvm.JvmInline

@Suppress("NOTHING_TO_INLINE")
inline fun String.toFeedId(): FeedId? =
    try {
        FeedId(this)
    } catch (e: IllegalArgumentException) {
        null
    }

@Suppress("NOTHING_TO_INLINE")
inline fun FeedId.youtubeVideoId(): String =
    value.replace("yt:video:","")

@Suppress("NOTHING_TO_INLINE")
inline fun FeedId.youtubeFeedIds(): List<FeedId> =
    listOf(
        this,
        FeedId("yt:channel:${value}"),
        FeedId("yt:playlist:${value}")
    )

@JvmInline
value class FeedId(val value: String) {
    init {
        require(value.isNotEmpty()) {
            "FeedId cannot be empty"
        }
    }
}