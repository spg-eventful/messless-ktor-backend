package at.eventful.messless.plugins.socket.model

/**
 * CRUD Methods used by the WebSocket messaging pipeline.
 *
 * The services do not interface with this, because they have a high level abstraction:
 * CREATE, FIND, GET, UPDATE, DELETE
 */
enum class Method {
    /** Insert a new entity */
    CREATE,

    /** Read one entity, if id is specified, or all entities if no id is specified */
    READ,

    /** Update fields of an entity (partial update) */
    UPDATE,

    /** Delete an entity */
    DELETE,
}