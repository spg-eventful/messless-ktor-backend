package at.eventful.messless.util

import at.eventful.messless.plugins.db.DatabaseConfiguration
import io.ktor.server.config.*
import io.ktor.util.logging.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

internal val DB_LOGGER = KtorSimpleLogger("Database")

@OptIn(ExperimentalDatabaseMigrationApi::class)
fun createCurrentMigrationScript(config: ApplicationConfig, vararg tables: Table) {
    val name = config.property("messless.db.migrations.current").getString()
    MigrationUtils.generateMigrationScript(
        *tables, scriptDirectory = config.property("messless.db.migrations.dir").getString(), scriptName = name
    )
    DB_LOGGER.info("Migration script $name created successfully!")
}

fun migrateWithFlyway(config: ApplicationConfig, baselineOnMigrate: Boolean = true) {
    val dbConfig = DatabaseConfiguration.fromApplicationConfig(config)
    val flyway = Flyway.configure()
        .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
        .locations("filesystem:${config.property("messless.db.migrations.dir").getString()}")
        .baselineOnMigrate(baselineOnMigrate)
        .load()
    flyway.migrate()
}