package at.eventful.messless.services.auth

import de.mkammerer.argon2.Argon2

fun Argon2.hashWithDefaultConfig(plaintext: String): String {
    return hash(10, 65536, 1, plaintext.toCharArray())
}