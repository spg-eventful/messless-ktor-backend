package at.eventful.messless.plugins.db

import at.eventful.messless.schema.tables.*
import at.eventful.messless.util.createDump
import at.eventful.messless.util.migrateWithFlyway
import io.ktor.server.application.*
import io.ktor.server.config.*
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

data class DatabaseConfiguration(val url: String, val user: String, val password: String) {
    companion object {
        fun fromApplicationConfig(config: ApplicationConfig): DatabaseConfiguration {
            config.property("ktor.environment").getString()
            return DatabaseConfiguration(
                config.property("messless.db.url").getString(),
                config.property("messless.db.user").getString(),
                config.property("messless.db.password").getString(),
            )
        }
    }
}

fun Application.configureDatabases() {
    val config = environment.config.config("messless.db")

    val url = config.property("url").getString()
    val user = config.property("user").getString()
    val password = config.property("password").getString()
    val driver = config.property("driver").getString()

    connectWithRetry(
        url = url,
        driver = driver,
        user = user,
        password = password
    )

    transaction {
        createDump(
            environment.config,
            CompanyTable,
            EquipmentStorageTable,
            EquipmentTable,
            EventTable,
            TechnicalLogEntryTable,
            UserTable,
            WarehouseTable
        )
        migrateWithFlyway(environment.config, baselineOnMigrate = true)
    }
}

fun connectWithRetry(
    url: String,
    driver: String,
    user: String,
    password: String
) {
    repeat(5) { attempt ->
        try {
            val db = Database.connect(
                url = url,
                driver = driver,
                user = user,
                password = password
            )

            transaction(db) {
                exec("SELECT 1")
            }

            println("Connected to DB")
            return
        } catch (e: Exception) {
            println("DB not ready yet (attempt: ${attempt + 1})")
            Thread.sleep(1000)
        }
    }
    throw RuntimeException("Could not connect to DB after retries")
}