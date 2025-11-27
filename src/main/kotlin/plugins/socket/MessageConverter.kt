package at.eventful.messless.plugins.socket

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method

/**
 * Converts message in a string format to an [IncomingMessage]
 */
class MessageConverter() {
    data class MessageConversionError(val messageToConvert: String, val error: String) :
        Error("Failed to convert message \"$messageToConvert\". $error")

    /**
     * Takes in a [message] argument and converts it to an [at.eventful.messless.plugins.socket.model.IncomingMessage].
     *
     * A message is formatted like this: `ID;METHOD;SERVICE;BODY?`, where the `?` signifies optionality.
     *
     * Some valid message examples:
     * ```
     * 0;CREATE;USERS;{}    <-  this is a create    request
     * 1;READ;USERS;1       <-  this ia a get       request
     * 2;READ;USERS;        <-  this is a find      request
     * ```
     *
     * @throws at.eventful.messless.plugins.socket.model.IncomingMessage When conversion fails due to a malformed message
     */
    @Throws(MessageConversionError::class)
    fun deserialize(message: String): IncomingMessage {
        val components = message.trim().split(';')
        if (components.size < 3) throw MessageConversionError(
            message,
            "The message does not conform to the scheme: ID;METHOD;SERVICE;BODY! Only ${components.size} message components found when at least 3 are expected!"
        )

        val id = components[0].toIntOrNull() ?: throw MessageConversionError(
            message,
            "ID is not a valid integer!"
        )

        val method = try {
            Method.valueOf(components[1].uppercase())
        } catch (_: IllegalArgumentException) {
            throw MessageConversionError(
                message,
                "Invalid method! Allowed methods are: ${Method.entries}!"
            )
        }

        // The service is not validated here, it is validated later, when we try to route the req.
        val service = components[2].trim().lowercase()
        if (service.isEmpty()) throw MessageConversionError(message, "No service specified")

        val body = if (components.size == 4) components[3].trim() else null

        return IncomingMessage(
            id,
            service,
            method,
            body
        )
    }
}