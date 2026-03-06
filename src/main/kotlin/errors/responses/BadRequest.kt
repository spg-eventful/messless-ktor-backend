package at.eventful.messless.errors.responses

import at.eventful.messless.errors.WebSocketErrorResponse
import io.ktor.http.*

class BadRequest(message: String? = null) :
    WebSocketErrorResponse(
        HttpStatusCode.BadRequest,
        "Request body could not be read properly${if (message == null) "." else ": $message"}",
        BadRequest::class
    )