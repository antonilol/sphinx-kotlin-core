package chat.sphinx.concepts.network.query.transport_key.model

import kotlinx.serialization.Serializable

@Serializable
data class RelayTransportKeyDto(
    val transport_key: String,
)