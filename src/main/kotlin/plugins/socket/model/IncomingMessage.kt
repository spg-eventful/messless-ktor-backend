package at.eventful.messless.plugins.socket.model

import at.eventful.messless.errors.responses.BadRequest
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.exposedLogger

/**
 * The message received by the server, decoded into its components
 */
data class IncomingMessage(
    val id: Int,
    val service: String,
    val method: Method,
    val body: String? = null
) {
    override fun toString(): String {
        return "$id;$method;$service${if (body == null) "" else ";$body"}"
    }

    /**
     * Decode the [body] into [T]. Correctly handles errors.
     * @throws BadRequest - when the [body] is null or not deserializable to [T]
     */
    inline fun <reified T> receiveBody(): T {
        if (body == null) throw BadRequest("body is null")
        try {
            return Json.decodeFromString<T>(body)
        } catch (e: SerializationException) {
            exposedLogger.debug("Serialization error (probably a client-side mistake): ", e)
            throw BadRequest(e.message)
        }
    }
}