package at.eventful.messless

import at.eventful.messless.plugins.db.configureDatabases
import at.eventful.messless.di.configureKoinDI
import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.index.registerIndexRoute
import at.eventful.messless.services.users.UsersService
import io.ktor.server.application.*
import org.koin.ktor.ext.get

val router = WebSocketRouter()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Install plugins
    configureKoinDI()
    configureDatabases()
    configureWebSocket()

    // Register HTTP routes
    registerIndexRoute()

    // Register WS routes
    router.removeAllRoutes()
    router.register(EchoService(), UsersService(get()))
}