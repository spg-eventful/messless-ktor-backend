package services.events

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.company.CompanyRepository
import at.eventful.messless.repositories.event.EventRepository
import at.eventful.messless.repositories.event.commands.CreateEventCmd
import at.eventful.messless.repositories.event.commands.UpdateEventCmd
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.schema.dao.CompanyDao
import at.eventful.messless.schema.dao.EventDao
import at.eventful.messless.schema.dao.LoggableDao
import io.ktor.client.plugins.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import repositories.users.UserRepository
import testutils.*

@ExtendWith(MockKExtension::class)
class EventServiceTest : AuthorizationTest() {
    val eventRepository = mockk<EventRepository>()
    override val usersRepository = mockk<UserRepository>()
    val loggableRepository = mockk<LoggableRepository>()
    val companyRepository = mockk<CompanyRepository>()

    companion object : AuthorizationTestCompanion() {
        val event = EventDao.fake(1)
        val loggable = LoggableDao.fake(1)
        val company = CompanyDao.fake(1)

        val updateCmd = UpdateEventCmd(
            event.id,
            loggable.label,
            loggable.longitude,
            loggable.latitude,
            company.id
        )

        val createCmd = CreateEventCmd(
            loggable.label,
            loggable.longitude,
            loggable.latitude,
            company.id
        )

        val updateLoggableCmd = UpdateLoggableCmd(
            loggable.id,
            loggable.label,
            loggable.longitude,
            loggable.latitude,
            loggable.loggableType,
            company.id
        )

        val createLoggableCmd = CreateLoggableCmd(
            loggable.label,
            loggable.longitude,
            loggable.latitude,
            loggable.loggableType,
            company.id
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq("create event", CompanyOne.admin, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("create event", CompanyOne.owner, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("create event", CompanyOne.stageHand, 403, Method.CREATE, Json.encodeToString(createCmd)),

            // READ
            ParameterizedReq("read event", CompanyOne.admin, 200, Method.READ, event.id.toString()),
            ParameterizedReq("read event", CompanyOne.owner, 200, Method.READ, event.id.toString()),
            ParameterizedReq("read event", CompanyOne.worker, 200, Method.READ, event.id.toString()),

            // READ ALL
            ParameterizedReq("reads all event", CompanyOne.admin, 200, Method.READ, null),
            ParameterizedReq("reads all event", CompanyOne.owner, 200, Method.READ, null),
            ParameterizedReq("reads all event", CompanyOne.worker, 200, Method.READ, null),

            // UPDATE
            ParameterizedReq("update event", CompanyOne.admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update event", CompanyOne.owner, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update event", CompanyOne.worker, 403, Method.UPDATE, Json.encodeToString(updateCmd)),

            // DELETE
            ParameterizedReq("delete event", CompanyOne.admin, 204, Method.DELETE, event.id.toString()),
            ParameterizedReq("delete event", CompanyOne.owner, 204, Method.DELETE, event.id.toString()),
            ParameterizedReq("delete event", CompanyOne.worker, 403, Method.DELETE, event.id.toString()),
        )
    }


    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(pr: ParameterizedReq) = configuredTestApplication {
        dependencies.provide<UserRepository> { usersRepository }
        dependencies.provide<EventRepository> { eventRepository }
        dependencies.provide<LoggableRepository> { loggableRepository }
        dependencies.provide<CompanyRepository> { companyRepository }
        every { eventRepository.addEvent(any()) } returns event
        every { eventRepository.allEvents() } returns listOf(event)
        every { eventRepository.eventById(event.id) } returns event
        every { eventRepository.updateEvent(event.id, updateCmd) } returns event
        every { eventRepository.removeEvent(event.id) } returns event
        every { loggableRepository.loggableById(loggable.id) } returns loggable
        every { loggableRepository.updateLoggable(loggable.id, updateLoggableCmd) } returns loggable
        every { loggableRepository.addLoggable(createLoggableCmd) } returns loggable
        every { loggableRepository.removeLoggable(loggable.id) } returns loggable

        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("events", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}