package at.eventful.messless

import at.eventful.messless.plugins.socket.WebSocketRouter
import at.eventful.messless.plugins.socket.configureWebSocket
import at.eventful.messless.schema.tables.*
import at.eventful.messless.services.echo.EchoService
import at.eventful.messless.services.index.registerIndexRoute
import at.eventful.messless.util.*
import io.ktor.server.application.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

val router = WebSocketRouter()

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module() {
    // Initialize database
    val database = Database.connect(URL, driver = "org.h2.Driver", USER, PASSWORD)

    // Install plugins
    configureWebSocket()

    // Register HTTP routes
    registerIndexRoute()

    // Register WS routes
    router.removeAllRoutes()
    router.register(EchoService())

    //create database migration script
    transaction(database) {
        createMigrationScript(
            CompanyTable, EquipmentStorageTable, EquipmentTable, EventTable, TechnicalLogEntryTable,
            UserTable, WarehouseTable, name = "V0.0.0__First_Migration"
        )
        //migrate database via flyway
        flywayMigrate(baselineOnMigrate = true)
    }
}