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
fun createDump(config: ApplicationConfig, vararg tables: Table) {
    MigrationUtils.generateMigrationScript(
        *tables,
        scriptDirectory = config.property("messless.db.dump.dir").getString(),
        scriptName = config.property("messless.db.dump.name").getString()
    )
    DB_LOGGER.info("DB dumps created! Do not forget to create a new db migration! This is not done automatically!")
}

fun migrateWithFlyway(config: ApplicationConfig, baselineOnMigrate: Boolean = false) {
    val dbConfig = DatabaseConfiguration.fromApplicationConfig(config)
    val flyway = Flyway.configure()
        .dataSource(dbConfig.url, dbConfig.user, dbConfig.password)
        .locations("filesystem:${config.property("messless.db.migrations.dir").getString()}")
        .baselineOnMigrate(baselineOnMigrate)
        .load()
    flyway.migrate()
}