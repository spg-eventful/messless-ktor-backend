package at.eventful.messless.plugins.socket.auth

import at.eventful.messless.schema.dao.UserDao
import java.time.Instant


data class AuthenticatedConnection(val expiresAt: Instant, val user: UserDao)

data class AuthenticationData(
    private var _isAuthenticated: Boolean = false,
    private var _auth: AuthenticatedConnection? = null
) {
    val isAuthenticated: Boolean get() = _isAuthenticated
    val auth: AuthenticatedConnection? get() = _auth

    fun grant(auth: AuthenticatedConnection) {
        _isAuthenticated = true
        _auth = auth
    }

    fun revoke() {
        _isAuthenticated = false
        _auth = null
    }
}
