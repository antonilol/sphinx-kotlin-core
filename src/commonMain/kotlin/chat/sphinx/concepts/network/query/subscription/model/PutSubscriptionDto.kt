package chat.sphinx.concepts.network.query.subscription.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PutSubscriptionDto(
    val amount: Long,
    val contact_id: Long,
    val chat_id: Long?,
    val end_number: Long?,
    val end_date: String?,
    val interval: String
)
