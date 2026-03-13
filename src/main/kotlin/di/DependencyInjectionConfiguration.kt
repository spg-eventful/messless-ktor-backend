package at.eventful.messless.di

import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.EquipmentRepositoryImpl
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import repositories.users.UserRepositoryImpl

fun Application.configureDI() {
    dependencies {
        provide<UserRepository> { UserRepositoryImpl() }
        provide<EquipmentRepository> { EquipmentRepositoryImpl() }
    }
}