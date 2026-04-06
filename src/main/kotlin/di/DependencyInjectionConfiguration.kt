package at.eventful.messless.di

import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.EquipmentRepositoryImpl
import at.eventful.messless.repositories.event.EventRepository
import at.eventful.messless.repositories.event.EventRepositoryImpl
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepository
import at.eventful.messless.repositories.technicalLogEntries.TechnicalLogEntryRepositoryImpl
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.repositories.warehouse.WarehouseRepositoryImpl
import at.eventful.messless.services.equipments.EquipmentsService
import at.eventful.messless.services.events.EventsService
import at.eventful.messless.services.technicalLogEntries.TechnicalLogEntriesService
import at.eventful.messless.services.users.UsersService
import at.eventful.messless.services.warehouse.WarehouseService
import de.mkammerer.argon2.Argon2
import de.mkammerer.argon2.Argon2Factory
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
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
        provide<EventRepository> { EventRepositoryImpl() }

        // Services
        provide<UsersService> { UsersService(this@configureDI) }
        provide<AuthService> { AuthService(this@configureDI, resolve<Argon2>()) }
        provide<WarehouseService> { WarehouseService(this@configureDI) }
        provide<TechnicalLogEntriesService> { TechnicalLogEntriesService(this@configureDI) }
        provide<EquipmentsService> { EquipmentsService(this@configureDI) }
        provide<EventsService> { EventsService(this@configureDI) }

        // Other
        provide<Argon2> { Argon2Factory.create() }
        provide<Dotenv> {
            dotenv {
                ignoreIfMalformed = true
                ignoreIfMissing = true
            }
        }
    }
}