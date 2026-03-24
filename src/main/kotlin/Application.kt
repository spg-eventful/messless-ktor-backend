package at.eventful.messless

import at.eventful.messless.di.configureDI
import at.eventful.messless.plugins.db.configureDatabases
import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.eqipments.EquipmentsService
import at.eventful.messless.services.events.EventsService
import at.eventful.messless.services.index.registerIndexRoute
import at.eventful.messless.services.users.UsersService
import at.eventful.messless.services.warehouse.WarehouseService
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import services.auth.AuthService

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
    router.register(
        EchoService(),
        dependencies.resolve<UsersService>(),
        dependencies.resolve<AuthService>(),
        dependencies.resolve<WarehouseService>(),
        dependencies.resolve<EquipmentsService>(),
        dependencies.resolve<EventsService>()
    )
}