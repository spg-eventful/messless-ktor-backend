package at.eventful.messless.schema.dao

import at.eventful.messless.schema.entities.LoggableEntity

interface ConvertibleDao<ENTITY_TYPE, RESULT> {
    fun from(entity: ENTITY_TYPE?, loggable: LoggableEntity? = null): RESULT? {
        return from(null, null)
    }

    fun from(entity: ENTITY_TYPE?): RESULT? {
        return from(null)
    }
}