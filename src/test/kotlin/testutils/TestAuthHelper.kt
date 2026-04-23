package testutils

import at.eventful.messless.plugins.socket.model.IncomingMessage
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.services.auth.AuthService
import at.eventful.messless.services.auth.JWTConfig
import at.eventful.messless.services.auth.commands.CreateAuthJWTCmd
import io.ktor.client.plugins.websocket.*
import io.ktor.server.application.*
import io.ktor.websocket.*
import kotlinx.serialization.json.Json
import kotlin.test.assertEquals

fun generateJWT(app: Application, user: UserDao): String {
    return AuthService.generateJWT(JWTConfig.fromConfig(app), user).first
}

suspend fun DefaultClientWebSocketSession.sendLoginFrame(ctx: MockApplicationContext, user: UserDao) {
    send(
        Frame.Text(
            IncomingMessage(
                0, "auth", Method.CREATE, Json.encodeToString(
                    CreateAuthJWTCmd(generateJWT(ctx.builder.application, user))
                )
            ).toString()
        )
    )
    val res = WebSocketResponse.fromString(receiveText())
    assertEquals(200, res.statusCode)
}
