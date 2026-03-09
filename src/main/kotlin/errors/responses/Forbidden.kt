package at.eventful.messless.errors.responses

import at.eventful.messless.errors.WebSocketErrorResponse
import io.ktor.http.*

class Forbidden(message: String? = null) :
    WebSocketErrorResponse(
        HttpStatusCode.Forbidden,
        "You are not allowed to access this resource${if (message == null) "." else ": $message"}",
        Forbidden::class
    )