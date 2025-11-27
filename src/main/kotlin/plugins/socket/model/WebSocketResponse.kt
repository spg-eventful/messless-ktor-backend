package at.eventful.messless.plugins.socket.model

import io.ktor.http.*
import io.ktor.websocket.*

/**
 * After processing a message, the WebSocket responds with a slightly different messaging schema:
 * `REQUEST_ID;STATUS_CODE;BODY?` where `?` signifies optionality
 *
 * Example:
 * ```
 * // Request (Incoming)
 * 0;CREATE;NOTES;{"message":"Hello, World"}
 *
 * // Response
 * 0;201;{"id":1,"message":"Hello,World"}
 * ```
 */
data class WebSocketResponse(
    val statusCode: Int,
    val body: String?,
    var id: Int? = null,
) {
    constructor(statusCode: HttpStatusCode, body: String?, id: Int? = null) : this(
        statusCode.value, body, id = id
    )

    /**
     * Convert a [WebSocketResponse] to a [Frame].
     *
     * @param id nullable, when present (not-null) overrides `this.id`. is used as the INCOMING_ID
     */
    fun toFrame(id: Int? = this.id): Frame {
        // TODO: Validate
        if (id == null && this.id == null) TODO("Unable to convert to frame with missing id. Set the id first!")
        if (id != null) this.id = id
        return Frame.Text("${this.id};$statusCode;$body")
    }
}