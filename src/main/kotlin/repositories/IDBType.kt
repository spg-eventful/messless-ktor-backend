package at.eventful.messless.repositories

interface IConvertibleDBType<ENTITY_TYPE, RESULT> {
    fun from(entity: ENTITY_TYPE?): RESULT?
}