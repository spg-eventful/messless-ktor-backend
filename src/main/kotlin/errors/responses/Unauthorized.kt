package at.eventful.messless.errors.responses

import at.eventful.messless.errors.WebSocketErrorResponse
import io.ktor.http.*

class Unauthorized(message: String? = null) :
    WebSocketErrorResponse(
        HttpStatusCode.Unauthorized,
        "Missing or bad authentication${if (message == null) "." else ": $message"}",
        Unauthorized::class
    )