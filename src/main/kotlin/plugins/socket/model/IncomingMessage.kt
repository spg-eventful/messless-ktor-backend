package at.eventful.messless.plugins.socket.model

/**
 * The message received by the server, decoded into its components
 */
data class IncomingMessage(
    val id: Int,
    val service: String,
    val method: Method,
    val body: String? = null
) {
    override fun toString(): String {
        return "$id;$method;$service;$body"
    }
}