package at.eventful.messless.errors.responses

import at.eventful.messless.errors.WebSocketErrorResponse
import io.ktor.http.*

class NotFound(message: String? = null) :
    WebSocketErrorResponse(
        HttpStatusCode.NotFound,
        "Unable to find resource${if (message == null) "." else ": $message"}",
        NotFound::class
    )