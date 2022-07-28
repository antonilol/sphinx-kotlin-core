package chat.sphinx.features.network.query.transport_key.model

import chat.sphinx.concepts.network.relay_call.RelayResponse
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.Serializable

@Serializable
data class AddRelayHMacKeyResponse(
    override val success: Boolean = true,
    override val response: @Polymorphic Any? = null,
    override val error: String? = null
): RelayResponse<Any>()