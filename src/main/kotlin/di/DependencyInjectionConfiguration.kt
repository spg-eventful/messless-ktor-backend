package at.eventful.messless.di

import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepositoryImpl
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import repositories.users.UserRepositoryImpl

fun Application.configureDI() {
    dependencies {
        provide<UserRepository> { UserRepositoryImpl() }
    }
    dependencies {
        provide<WarehouseRepository> { WarehouseRepositoryImpl() }
    }
}