package at.eventful.messless.schema.utils

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.dao.EntityBatchUpdate
import org.jetbrains.exposed.v1.dao.IntEntity
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
abstract class BaseEntity(id: EntityID<Int>) : IntEntity(id) {

    abstract var createdAt: Instant
    abstract var updatedAt: Instant?
    abstract var deletedAt: Instant?

    override fun flush(batch: EntityBatchUpdate?): Boolean {
        if (deletedAt == null) {
            this.updatedAt = Clock.System.now()
        }
        return super.flush(batch)
    }

    override fun delete() {
        this.deletedAt = Clock.System.now()
        this.flush()
    }
}