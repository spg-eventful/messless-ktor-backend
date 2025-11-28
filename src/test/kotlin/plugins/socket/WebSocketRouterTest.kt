package plugins.socket

import at.eventful.messless.errors.ServiceAlreadyRegistered
import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.services.echo.EchoService
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test


class WebSocketRouterTest {
    @Test
    fun serviceAlreadyRegistered() {
        val router = WebSocketRouter()
        assertThrows<ServiceAlreadyRegistered> { router.register(EchoService(), EchoService()) }
    }
}