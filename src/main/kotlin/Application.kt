package at.eventful.messless

import at.eventful.messless.di.configureDI
import at.eventful.messless.plugins.db.configureDatabases
import at.eventful.messless.plugins.db.seedDatabase
import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.equipments.EquipmentsService
import at.eventful.messless.services.events.EventsService
import at.eventful.messless.services.index.registerIndexRoute
import at.eventful.messless.services.technicalLogEntries.TechnicalLogEntriesService
import at.eventful.messless.services.users.UsersService
import at.eventful.messless.services.warehouse.WarehouseService
import de.mkammerer.argon2.Argon2
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.di.*
import services.auth.AuthService

val router = WebSocketRouter()

fun main(args: Array<String>) {
    val dotenv = dotenv {
        ignoreIfMalformed = true
        ignoreIfMissing = true
    }

    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }

    EngineMain.main(args)
}

suspend fun Application.module() {
    // Install plugins
    configureDI()
    val argon2 = dependencies.resolve<Argon2>()
    val dotenv = dependencies.resolve<Dotenv>()
    configureDatabases()
    seedDatabase(argon2, dotenv)
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
        dependencies.resolve<EventsService>(),
        dependencies.resolve<TechnicalLogEntriesService>()
    )
}
