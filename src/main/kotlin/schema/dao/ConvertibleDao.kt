package at.eventful.messless.schema.dao

interface ConvertibleDao<ENTITY_TYPE, RESULT> {
    fun from(entity: ENTITY_TYPE?): RESULT?
}