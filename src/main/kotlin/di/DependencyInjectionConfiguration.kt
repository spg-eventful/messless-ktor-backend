package at.eventful.messless.di

import at.eventful.messless.services.users.UsersService
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import repositories.users.UserRepositoryImpl

fun Application.configureDI() {
    dependencies {
        provide<UserRepository> { UserRepositoryImpl() }

        // Services
        provide<UsersService> { UsersService(this@configureDI) }
    }
}