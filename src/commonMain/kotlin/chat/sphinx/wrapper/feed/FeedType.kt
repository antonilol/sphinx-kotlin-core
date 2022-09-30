package chat.sphinx.wrapper.feed

@Suppress("NOTHING_TO_INLINE")
inline fun FeedType.isPodcast(): Boolean =
    this is FeedType.Podcast

@Suppress("NOTHING_TO_INLINE")
inline fun FeedType.isVideo(): Boolean =
    this is FeedType.Video

@Suppress("NOTHING_TO_INLINE")
inline fun FeedType.isNewsletter(): Boolean =
    this is FeedType.Newsletter

@Suppress("NOTHING_TO_INLINE")
inline fun FeedType.toFeedTypeString(): String =
    when (this) {
        FeedType.Podcast -> {
            "Podcast "
        }
        FeedType.Newsletter -> {
            "Newsletter"
        }
        FeedType.Video -> {
            "Video"
        }
        else -> {
            ""
        }
    }

@Suppress("NOTHING_TO_INLINE")
inline fun Int.toFeedType(): FeedType =
    when (this) {
        FeedType.PODCAST -> {
            FeedType.Podcast
        }
        FeedType.VIDEO -> {
            FeedType.Video
        }
        FeedType.NEWSLETTER -> {
            FeedType.Newsletter
        }
        else -> {
            FeedType.Unknown(this)
        }
    }

sealed class FeedType {

    companion object {
        const val PODCAST = 0 // SHOW
        const val VIDEO = 1
        const val NEWSLETTER = 2 // SHOW
    }

    abstract val value: Int

    object Podcast : FeedType() {
        override val value: Int
            get() = PODCAST
    }

    object Video : FeedType() {
        override val value: Int
            get() = VIDEO
    }

    object Newsletter : FeedType() {
        override val value: Int
            get() = NEWSLETTER
    }

    data class Unknown(override val value: Int) : FeedType()
}