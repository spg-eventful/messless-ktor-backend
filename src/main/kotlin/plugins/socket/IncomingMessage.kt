package at.eventful.messless.plugins.socket

data class IncomingMessage(
    val id: Int,
    val service: String,
    val method: Method,
    val body: String?
)