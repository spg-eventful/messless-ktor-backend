package plugins.socket.model

import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test

class WebSocketResponseTest {
    @Test
    fun toFrameWithId() {
        val wsRes = WebSocketResponse(
            HttpStatusCode.OK,
            HttpStatusCode.OK.description,
            0
        )

        val wsResNoId = WebSocketResponse(
            HttpStatusCode.OK,
            HttpStatusCode.OK.description,
        )

        assertDoesNotThrow { wsRes.toFrame() }
        assertDoesNotThrow { wsRes.toFrame(0) }
        assertDoesNotThrow { wsResNoId.toFrame(0) }
    }

    @Test
    fun toFrameWithoutId() {
        val wsRes = WebSocketResponse(
            HttpStatusCode.OK,
            HttpStatusCode.OK.description,
        )
        assertThrows<IllegalStateException> { wsRes.toFrame() }
    }
}