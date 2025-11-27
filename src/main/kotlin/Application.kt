package at.eventful.messless

import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.index.registerIndexRoute
import io.ktor.server.application.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Install plugins
    configureWebSocket()

    // Register routes
    registerIndexRoute()
}