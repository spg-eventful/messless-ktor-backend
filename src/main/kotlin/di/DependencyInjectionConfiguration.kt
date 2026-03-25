package at.eventful.messless.di

import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepositoryImpl
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.EquipmentRepositoryImpl
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepositoryImpl
import at.eventful.messless.services.eqipments.EquipmentsService
import at.eventful.messless.services.technicalLogEntries.TechnicalLogEntriesService
import at.eventful.messless.services.users.UsersService
import at.eventful.messless.services.warehouse.WarehouseService
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import repositories.users.UserRepository
import repositories.users.UserRepositoryImpl
import services.auth.AuthService

fun Application.configureDI() {
    dependencies {
        // Repositories
        provide<UserRepository> { UserRepositoryImpl(resolve<Argon2>()) }
        provide<WarehouseRepository> { WarehouseRepositoryImpl() }
        provide<TechnicalLogEntryRepository> { TechnicalLogEntryRepositoryImpl() }
        provide<EquipmentRepository> { EquipmentRepositoryImpl() }

        // Services
        provide<UsersService> { UsersService(this@configureDI) }
        provide<AuthService> { AuthService(this@configureDI, resolve<Argon2>()) }
        provide<WarehouseService> { WarehouseService(this@configureDI) }
        provide<TechnicalLogEntriesService> { TechnicalLogEntriesService(this@configureDI) }
        provide<EquipmentsService> { EquipmentsService(this@configureDI) }

        // Other
        provide<Argon2> { Argon2Factory.create() }
    }
}