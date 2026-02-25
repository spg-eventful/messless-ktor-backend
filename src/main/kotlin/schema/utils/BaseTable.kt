package at.eventful.messless.schema.utils

import org.jetbrains.exposed.v1.core.dao.id.IntIdTable
import org.jetbrains.exposed.v1.datetime.timestamp
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
abstract class BaseTable(name: String) : IntIdTable(name) {

    val createdAt = timestamp("created_at").clientDefault { Clock.System.now() }
    val updatedAt = timestamp("updated_at").nullable()
    val deletedAt = timestamp("deleted_at").nullable()

}