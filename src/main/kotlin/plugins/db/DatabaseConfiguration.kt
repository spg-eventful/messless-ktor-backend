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
            val env = config.property("ktor.environment").getString()
            return DatabaseConfiguration(
                config.property("messless.db.$env.url").getString(),
                config.property("messless.db.$env.user").getString(),
                config.property("messless.db.$env.password").getString(),
            )
        }
    }
}

fun Application.configureDatabases() {
    val conf = DatabaseConfiguration.fromApplicationConfig(environment.config)
    Database.connect(
        url = conf.url, user = conf.user, password = conf.password
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