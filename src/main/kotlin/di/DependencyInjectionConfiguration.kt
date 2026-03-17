package at.eventful.messless.di

import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.EquipmentRepositoryImpl
import at.eventful.messless.services.users.UsersService
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import repositories.users.UserRepositoryImpl
import services.auth.AuthService

fun Application.configureDI() {
    dependencies {
        provide<UserRepository> { UserRepositoryImpl(resolve<Argon2>()) }

        // Services
        provide<UsersService> { UsersService(this@configureDI) }
        provide<AuthService> { AuthService(this@configureDI, resolve<Argon2>()) }
        provide<Argon2> { Argon2Factory.create() }
        provide<EquipmentRepository> { EquipmentRepositoryImpl() }
    }
}