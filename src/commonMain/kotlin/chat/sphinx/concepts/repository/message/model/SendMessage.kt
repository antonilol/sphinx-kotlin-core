package chat.sphinx.concepts.repository.message.model

import chat.sphinx.utils.platform.File
import chat.sphinx.wrapper.dashboard.ChatId
import chat.sphinx.wrapper.dashboard.ContactId
import chat.sphinx.wrapper.lightning.Sat
import chat.sphinx.wrapper.message.GiphyData
import chat.sphinx.wrapper.message.PodcastClip
import chat.sphinx.wrapper.message.ReplyUUID
import chat.sphinx.wrapper.message.media.isSphinxText
import kotlin.jvm.Synchronized

class SendMessage private constructor(
    val attachmentInfo: AttachmentInfo?,
    val chatId: ChatId?,
    val contactId: ContactId?,
    val replyUUID: ReplyUUID?,
    val text: String?,
    val giphyData: GiphyData?,
    val podcastClip: PodcastClip?,
    val isBoost: Boolean,
    val messagePrice: Sat?,
    val priceToMeet: Sat?
) {

    class Builder {
        private var chatId: ChatId?                 = null
        private var contactId: ContactId?           = null
        private var attachmentInfo: AttachmentInfo? = null
        private var replyUUID: ReplyUUID?           = null
        private var text: String?                   = null
        private var giphyData: GiphyData?           = null
        private var podcastClip: PodcastClip?       = null
        private var isBoost: Boolean                = false
        private var messagePrice: Sat?              = null
        private var priceToMeet: Sat?               = null

        enum class ValidationError {
            EMPTY_PRICE, EMPTY_DESTINATION, EMPTY_CONTENT
        }

        @Synchronized
        fun clear() {
            attachmentInfo = null
            chatId = null
            contactId = null
            replyUUID = null
            text = null
            giphyData = null
            podcastClip = null
            isBoost = false
            messagePrice = null
            priceToMeet = null
        }

        @Synchronized
        fun isValid(): Pair<Boolean, ValidationError?> {
            if (chatId == null && contactId == null) {
                return Pair(false, ValidationError.EMPTY_DESTINATION)
            }

            val file: File? = attachmentInfo?.file?.let {
                try {
                    if (it.exists() && it.isFile()) {
                        it
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    null
                }
            }

            when {
                (file == null) -> {
                    if (
                        text.isNullOrEmpty() &&
                        giphyData == null &&
                        podcastClip == null
                    ) {
                        return Pair(false, ValidationError.EMPTY_CONTENT)
                    }
                }
                else -> {
                    val isPaidTextMessage = attachmentInfo?.mediaType?.isSphinxText == true
                    val messagePrice = messagePrice?.value ?: 0

                    if (isPaidTextMessage && messagePrice == 0.toLong()) {
                        return Pair(false, ValidationError.EMPTY_PRICE)
                    }
                }
            }

            return Pair(true, null)
        }

        @Synchronized
        fun setAttachmentInfo(attachmentInfo: AttachmentInfo?): Builder {
            this.attachmentInfo = attachmentInfo
            return this
        }

        @Synchronized
        fun setChatId(chatId: ChatId?): Builder {
            this.chatId = chatId
            return this
        }

        @Synchronized
        fun setContactId(contactId: ContactId?): Builder {
            this.contactId = contactId
            return this
        }

        @Synchronized
        fun setReplyUUID(replyUUID: ReplyUUID?): Builder {
            this.replyUUID = replyUUID
            return this
        }

        @Synchronized
        fun setText(text: String?): Builder {
            if (text == null || text.isEmpty()) {
                this.text = null
            } else {
                this.text = text
            }
            return this
        }

        @Synchronized
        fun setGiphyData(giphyData: GiphyData?): Builder {
            this.giphyData = giphyData
            return this
        }

        @Synchronized
        fun setPodcastClip(podcastClip: PodcastClip?): Builder {
            this.podcastClip = podcastClip
            return this
        }

        @Synchronized
        fun setIsBoost(isBoost: Boolean): Builder {
            this.isBoost = isBoost
            return this
        }

        @Synchronized
        fun setPriceToMeet(priceToMeet: Sat?): Builder {
            this.priceToMeet = priceToMeet
            return this
        }

        @Synchronized
        fun setMessagePrice(messagePrice: Sat?): Builder {
            this.messagePrice = messagePrice
            return this
        }

        @Synchronized
        fun build(): Pair<SendMessage?, ValidationError?> {
            val isValid = isValid()

            if (!isValid.first) {
                return Pair(null, isValid.second)
            } else {
                return Pair(
                    SendMessage(
                        attachmentInfo,
                        chatId,
                        contactId,
                        replyUUID,
                        text,
                        giphyData?.let { GiphyData(it.id, it.url, it.aspect_ratio, text) },
                        podcastClip?.let { PodcastClip(text, it.title, it.pubkey, it.url, it.feedID, it.itemID, it.ts) },
                        isBoost,
                        messagePrice,
                        priceToMeet
                    ), null
                )
            }
        }
    }
}
