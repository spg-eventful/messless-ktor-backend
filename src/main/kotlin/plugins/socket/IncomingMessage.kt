package at.eventful.messless.plugins.socket

data class IncomingMessage(
    val id: Int,
    val service: String,
    val method: MessageConverter.CrudMethods,
    val body: String?
)