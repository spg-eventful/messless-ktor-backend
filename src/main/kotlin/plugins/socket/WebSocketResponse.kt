package at.eventful.messless.plugins.socket

import io.ktor.http.*
import io.ktor.websocket.*

data class WebSocketResponse(
    val id: Int,
    val statusCode: Int,
    val body: String?
) {
    constructor(id: Int, statusCode: HttpStatusCode, body: String?) : this(
        id, statusCode.value, body,
    )

    fun toFrame(): Frame {
        // TODO: Validate
        return Frame.Text("$id;$statusCode;$body")
    }
}