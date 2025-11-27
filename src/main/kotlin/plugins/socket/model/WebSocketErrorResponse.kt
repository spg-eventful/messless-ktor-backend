package at.eventful.messless.plugins.socket.model

import io.ktor.http.*

data class WebSocketErrorResponse(
    val code: Int,
    override val message: String,
) : Error() {
    constructor(code: HttpStatusCode, message: String) : this(code.value, message)

    fun toWebSocketResponse(): WebSocketResponse = WebSocketResponse(code, message)
}