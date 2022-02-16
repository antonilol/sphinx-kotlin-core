package chat.sphinx.concepts.network.query.chat.model.feed

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FeedValueDto(
    val model: FeedModelDto,
    val destinations: List<FeedDestinationDto>,
)