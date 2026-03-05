package at.eventful.messless.util

import io.ktor.server.application.*
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.migration.jdbc.MigrationUtils
import java.io.File

const val MIGRATIONS_DIRECTORY = "../../resources/db/migration/"
const val URL = "jdbc:h2:mem:messlessBackendTest"
const val USER = ""
const val PASSWORD = ""

//define what scripts you want the table to be created for
@OptIn(ExperimentalDatabaseMigrationApi::class)
fun Application.createMigrationScript(vararg tables: Table, name: String) {
    MigrationUtils.generateMigrationScript(
        *tables, scriptDirectory = MIGRATIONS_DIRECTORY, scriptName = name
    )
}

//create a script for all tables in the /tables
@OptIn(ExperimentalDatabaseMigrationApi::class)
fun createMigrationScript(name: String) {
    val files = File("/schema/tables/").listFiles()

    MigrationUtils.generateMigrationScript(
        files as Table, scriptDirectory = MIGRATIONS_DIRECTORY, scriptName = name
    )
}

fun flywayMigrate(baselineOnMigrate: Boolean) {
    val flyway = Flyway.configure()
        .dataSource(URL, USER, PASSWORD)
        .locations("filesystem:$MIGRATIONS_DIRECTORY")
        .baselineOnMigrate(baselineOnMigrate)
        .load()
    flyway.migrate()
}