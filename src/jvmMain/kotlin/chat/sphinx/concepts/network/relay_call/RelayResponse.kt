package chat.sphinx.concepts.network.relay_call

/**
 * A wrapper for Relay specific responses. This should *never* be exposed
 * to concept modules, as [T] should be what is returned.
 * */
abstract class RelayResponse<T: Any> {
    abstract val success: Boolean
    abstract val response: T?
    abstract val error: String?
}
