package testutils

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.server.config.*
import io.ktor.server.testing.*

class MockApplicationContext(val builder: ApplicationTestBuilder) {
    val client: HttpClient by lazy {
        builder.createClient {
            install(WebSockets)
        }
    }
}

fun configuredTestApplication(block: suspend MockApplicationContext.() -> Unit) = testApplication {
    environment {
        config = ApplicationConfig("application.yaml")
    }

    val ctx = MockApplicationContext(this)
    ctx.block()
}