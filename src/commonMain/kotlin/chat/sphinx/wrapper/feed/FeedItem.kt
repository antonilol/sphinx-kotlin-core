package chat.sphinx.wrapper.feed

import chat.sphinx.utils.platform.File
import chat.sphinx.wrapper.DateTime
import chat.sphinx.wrapper.PhotoUrl

inline val FeedItem.isPodcast: Boolean
    get() = feed?.feedType?.isPodcast() == true

inline val FeedItem.isVideo: Boolean
    get() = feed?.feedType?.isVideo() == true

inline val FeedItem.isNewsletter: Boolean
    get() = feed?.feedType?.isNewsletter() == true

data class FeedItem(
    override val id: FeedId, // TODO: Should this be FeedItemId?
    val title: FeedTitle,
    val description: FeedDescription?,
    val datePublished: DateTime?,
    val dateUpdated: DateTime?,
    val author: FeedAuthor?,
    val contentType: FeedContentType?,
    override val enclosureLength: FeedEnclosureLength?,
    override val enclosureUrl: FeedUrl,
    override val enclosureType: FeedEnclosureType?,
    val imageUrl: PhotoUrl?,
    val thumbnailUrl: PhotoUrl?,
    val link: FeedUrl?,
    val feedId: FeedId,
    val duration: FeedItemDuration?,
    override var localFile: File?
): DownloadableFeedItem {

    var feed: Feed? = null

    var itemImageUrlToShow: PhotoUrl? = null
        get() {
            imageUrl?.let {
                return it
            }
            return thumbnailUrlToShow
        }

    var imageUrlToShow: PhotoUrl? = null
        get() {
            imageUrl?.let {
                return it
            }
            return null
        }

    var thumbnailUrlToShow: PhotoUrl? = null
        get() {
            thumbnailUrl?.let {
                return it
            }
            return null
        }

    var titleToShow: String = ""
        get() = title.value.trim()

    var descriptionToShow: String = ""
        get() {
            return (description?.value ?: feed?.description?.value ?: "").htmlToPlainText().trim()
        }

    val downloaded: Boolean
        get()= localFile != null
}