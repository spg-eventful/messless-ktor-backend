package at.eventful.messless.plugins.socket.model

import io.ktor.http.*
import io.ktor.websocket.*

data class WebSocketResponse(
    val statusCode: Int,
    val body: String?,
    var id: Int? = null,
) {
    constructor(statusCode: HttpStatusCode, body: String?, id: Int? = null) : this(
        statusCode.value, body, id = id
    )

    fun toFrame(id: Int? = this.id): Frame {
        // TODO: Validate
        if (id == null && this.id == null) TODO("Unable to convert to frame with missing id. Set the id first!")
        if (id != null) this.id = id
        return Frame.Text("${this.id};$statusCode;$body")
    }
}