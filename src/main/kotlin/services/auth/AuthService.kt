package services.auth

import at.eventful.messless.errors.responses.BadRequest
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.auth.AuthenticatedConnection
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.schema.dao.UserDao
import at.eventful.messless.schema.dto.AuthDto
import at.eventful.messless.schema.dto.UserDto
import at.eventful.messless.services.auth.commands.CreateAuthBasicCmd
import at.eventful.messless.services.auth.commands.CreateAuthJWTCmd
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import de.mkammerer.argon2.Argon2
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import java.util.*

data class JWTConfig(val secret: String, val issuer: String, val audience: String, val realm: String) {
    companion object {
        fun fromConfig(app: Application): JWTConfig = JWTConfig(
            app.environment.config.property("messless.jwt.secret").getString(),
            app.environment.config.property("messless.jwt.issuer").getString(),
            app.environment.config.property("messless.jwt.audience").getString(),
            app.environment.config.property("messless.jwt.realm").getString(),
        )
    }
}

class AuthService(app: Application, val argon2: Argon2) : WebSocketService("auth") {
    val usersRepo: UserRepository by app.dependencies
    val jwtConfig = JWTConfig.fromConfig(app)

    /**
     * Authenticates an existing JWT via the [CreateAuthJWTCmd] or creates a new JWT via the [CreateAuthBasicCmd].
     */
    override fun ServiceMethod.create(): WebSocketResponse<AuthDto> {
        val cmd = runCatching { incoming.receiveBody<CreateAuthJWTCmd>() }.getOrNull()
            ?: incoming.receiveBody<CreateAuthBasicCmd>()

        if (cmd is CreateAuthJWTCmd) {
            val jwt = runCatching { JWT.decode(cmd.jwt) }.getOrNull() ?: throw BadRequest("unable to decode jwt")
            if (jwt.expiresAt.before(Date())) throw Unauthorized("jwt expired")

            val userId = jwt.getClaim("id").asInt() ?: throw Unauthorized("id must be a parsable integer")
            val user = usersRepo.userById(userId) ?: throw Unauthorized("user with id $userId not found")

            connection.auth.grant(AuthenticatedConnection(jwt.expiresAtAsInstant, user))
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                AuthDto(cmd.jwt, UserDto.from(user))
            )
        }

        if (cmd !is CreateAuthBasicCmd) throw BadRequest("request body must either be an object {jwt: \"token\"} or basic auth(email, password) [json]")
        val user = usersRepo.userByEmail(cmd.email) ?: throw Unauthorized()
        if (!argon2.verify(user.password, cmd.password.toCharArray())) throw Unauthorized()

        // authenticates the current session
        val (jwt, expiry) = generateJWT(jwtConfig, user)
        connection.auth.grant(AuthenticatedConnection(expiry.toInstant(), user))

        // grant access token
        // TODO: Refresh token
        return WebSocketResponse.from(
            HttpStatusCode.Created,
            AuthDto(jwt, UserDto.from(user))
        )
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<*> {
        TODO("Exchange refresh token for a new access token and rotate refresh token.")
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<Nothing> {
        TODO("Revoke refresh token / logout")
    }

    companion object {
        fun generateJWT(config: JWTConfig, user: UserDao): Pair<String, Date> {
            val expiry = Date(System.currentTimeMillis() + 60000)
            return Pair(
                JWT.create()
                    .withAudience(config.audience)
                    .withIssuer(config.issuer)
                    .withSubject(user.email)
                    .withClaim("id", user.id)
                    .withExpiresAt(expiry)
                    .sign(Algorithm.HMAC256(config.secret)),
                expiry
            )
        }
    }
}