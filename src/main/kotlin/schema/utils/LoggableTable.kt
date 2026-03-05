package at.eventful.messless.schema.utils


abstract class LoggableTable(name: String) : BaseTable(name) {

    val label = varchar("label", 255)
    val location = point("location", srid = 4326)

    /*
    Idee:
    fun setupDatabaseHooks() {
    EntityHook.subscribe { action ->
        if (action.changeType == EntityChangeType.Updated) {
            val entity = action.toEntity() as? ITimestampedEntity
            entity?.updatedAt = LocalDateTime.now()
        }
    }
}
     */

}