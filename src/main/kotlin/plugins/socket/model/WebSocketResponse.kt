package at.eventful.messless.plugins.socket.model

import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json

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
data class WebSocketResponse<RESPONSE_TYPE>(
    val statusCode: Int,
    val body: String? = null,
    var id: Int? = null,
) {
    constructor(statusCode: HttpStatusCode, body: String? = null, id: Int? = null) : this(
        statusCode.value, body, id = id
    )


    /**
     * The response is not a valid [WebSocketResponse] and can not be converted into one.
     */
    data class InvalidWebSocketResponse(val s: String) :
        Error("$s is not a valid WebSocketResponse and cannot be parsed as one.")

    /**
     * Convert a [WebSocketResponse] to a [Frame].
     *
     * @param id nullable, when present (not-null) overrides `this.id`. is used as the INCOMING_ID
     */
    fun toFrame(id: Int? = this.id): Frame {
        if (id == null) throw IllegalStateException("Unable to convert to frame without an id! Set the id first or pass it!")
        return Frame.Text("${id};$statusCode;$body")
    }

    companion object {

        /**
         * Convert a serialized [WebSocketResponse] (stringified) into a [WebSocketResponse] instance.
         * @throws InvalidWebSocketResponse if the response is not valid
         */
        @JvmName("fromStringTyped")
        fun <RESPONSE_TYPE> fromString(s: String): WebSocketResponse<RESPONSE_TYPE> =
            fromString(s) as WebSocketResponse<RESPONSE_TYPE>

        /**
         * Convert a serialized [WebSocketResponse] (stringified) into a [WebSocketResponse] instance.
         * @throws InvalidWebSocketResponse if the response is not valid
         */
        fun fromString(s: String): WebSocketResponse<Any> {
            return try {
                val components = s.split(";")

                val id = components[0].toInt()
                val statusCode = components[1].toInt()
                val body = components[2]

                WebSocketResponse(statusCode, body, id)
            } catch (_: Error) {
                throw InvalidWebSocketResponse(s)
            }
        }

        /**
         * Create a [WebSocketResponse] with a body that is json encoded to a string.
         */
        inline fun <reified RESPONSE_TYPE> from(
            statusCode: HttpStatusCode,
            body: RESPONSE_TYPE,
            id: Int? = null
        ): WebSocketResponse<RESPONSE_TYPE> =
            WebSocketResponse(statusCode, Json.encodeToString(body), id)
    }
}