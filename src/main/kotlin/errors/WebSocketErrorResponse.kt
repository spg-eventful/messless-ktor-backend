package at.eventful.messless.errors

import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*

open class WebSocketErrorResponse(
    val code: Int,
    override val message: String,
) : Error() {
    constructor(code: HttpStatusCode, message: String) : this(code.value, message)

    fun toWebSocketResponse(): WebSocketResponse = WebSocketResponse(code, message)
}