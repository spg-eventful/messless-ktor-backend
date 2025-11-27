package at.eventful.messless.plugins.socket

import io.ktor.http.*

data class WebSocketResponse(
    val id: Int,
    val statusCode: Int,
    val body: String?
) {
    constructor(id: Int, statusCode: HttpStatusCode, body: String?) : this(
        id, statusCode.value, body,
    )
}