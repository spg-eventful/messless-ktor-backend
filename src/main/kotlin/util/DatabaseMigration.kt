package at.eventful.messless.util

import io.ktor.util.logging.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils

internal val DB_LOGGER = KtorSimpleLogger("Database")

const val MIGRATIONS_DIRECTORY = "src/main/resources/db/migrations/"
const val URL = "jdbc:h2:mem:messlessBackendTest"
const val USER = ""
const val PASSWORD = ""

//define what scripts you want the table to be created for
@OptIn(ExperimentalDatabaseMigrationApi::class)
fun createMigrationScript(vararg tables: Table, name: String) {
    MigrationUtils.generateMigrationScript(
        *tables, scriptDirectory = MIGRATIONS_DIRECTORY, scriptName = name
    )
    DB_LOGGER.info("Migration script $name created successfully!")
}

fun flywayMigrate(baselineOnMigrate: Boolean) {
    val flyway = Flyway.configure()
        .dataSource(URL, USER, PASSWORD)
        .locations("filesystem:$MIGRATIONS_DIRECTORY")
        .baselineOnMigrate(baselineOnMigrate)
        .load()
    flyway.migrate()
}