package at.eventful.messless.schema.utils

import net.postgis.jdbc.geometry.Point
import org.jetbrains.exposed.v1.core.dao.id.EntityID


abstract class LoggableEntity(id: EntityID<Int>) : BaseEntity(id) {

    abstract var label: String
    abstract var location: Point

}