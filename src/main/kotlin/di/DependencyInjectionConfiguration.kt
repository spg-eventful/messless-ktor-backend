package at.eventful.messless.di

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UsersRepository
import repositories.users.UsersRepositoryImpl

fun Application.configureDI() {
    dependencies {
        provide<UsersRepository> { UsersRepositoryImpl() }
    }
}