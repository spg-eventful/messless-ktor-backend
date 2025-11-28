package at.eventful.messless.errors

import at.eventful.messless.plugins.socket.model.WebSocketResponse
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass



open class WebSocketErrorResponse(
    val code: Int,
    override val message: String,
    val errorClass: KClass<out Any> = WebSocketErrorResponse::class,
) : Error() {
    constructor(
        code: HttpStatusCode,
        message: String,
        errorClass: KClass<out Any> = WebSocketErrorResponse::class
    ) : this(code.value, message, errorClass)

    @Serializable
    internal data class ErrorResponse(val message: String, val errorClass: String)

    fun toWebSocketResponse(): WebSocketResponse = WebSocketResponse(
        code,
        Json.encodeToString(
            ErrorResponse(
                message,
                errorClass.simpleName ?: "Unknown",
            )
        )
    )
}