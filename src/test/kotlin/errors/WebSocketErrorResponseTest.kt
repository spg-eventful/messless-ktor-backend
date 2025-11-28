package errors

import at.eventful.messless.errors.WebSocketErrorResponse
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class WebSocketErrorResponseTest {
    @Test
    fun toWebSocketResponse() {
        val badRequest = WebSocketErrorResponse(
            HttpStatusCode.BadRequest,
            HttpStatusCode.BadRequest.description,
        )
        val withObject = WebSocketErrorResponse(
            HttpStatusCode.BadRequest,
            HttpStatusCode.BadRequest.description,
            (object : Error() {})::class
        )

        val baseClass = Json.decodeFromString<WebSocketErrorResponse.ErrorResponse>(
            badRequest.toWebSocketResponse().body ?: ""
        )

        val anonymousClass = Json.decodeFromString<WebSocketErrorResponse.ErrorResponse>(
            withObject.toWebSocketResponse().body ?: ""
        )

        assertEquals(WebSocketErrorResponse::class.simpleName, baseClass.errorClass)
        assertEquals("Unknown", anonymousClass.errorClass)
    }
}