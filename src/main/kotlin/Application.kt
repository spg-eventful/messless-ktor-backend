package at.eventful.messless

import at.eventful.messless.di.configureDI
import at.eventful.messless.plugins.db.configureDatabases
import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.index.registerIndexRoute
import at.eventful.messless.services.users.UsersService
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

val router = WebSocketRouter()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

suspend fun Application.module() {
    // Install plugins
    configureDI()
    configureDatabases()
    configureWebSocket()

    // Register HTTP routes
    registerIndexRoute()

    // Register WS routes
    router.removeAllRoutes()
    router.register(EchoService(), UsersService(this))
}