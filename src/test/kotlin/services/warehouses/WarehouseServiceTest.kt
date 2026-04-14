package services.warehouses

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dao.LoggableDao
import at.eventful.messless.schema.dao.WarehouseDao
import io.ktor.client.plugins.websocket.*
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import repositories.users.UserRepository
import repositories.warehouse.command.CreateWarehouseCmd
import repositories.warehouse.command.UpdateWarehouseCmd
import testutils.*

@ExtendWith(MockKExtension::class)
class WarehouseServiceTest : AuthorizationTest() {
    val warehouseRepository = mockk<WarehouseRepository>()
    override val usersRepository = mockk<UserRepository>()
    val loggableRepository = mockk<LoggableRepository>()

    companion object : AuthorizationTestCompanion() {
        val warehouse = WarehouseDao.fake(1)
        val loggable = LoggableDao.fake(1)

        val createCmd = CreateWarehouseCmd(
            loggable.label,
            loggable.latitude,
            loggable.longitude,
            CompanyOne.owner.company?.id ?: 1,
        )

        val updateCmd = UpdateWarehouseCmd(
            warehouse.id,
            loggable.label,
            loggable.latitude,
            loggable.longitude,
            CompanyOne.owner.company?.id ?: 1,
        )

        val updateLoggableCmd = UpdateLoggableCmd(
            loggable.id,
            loggable.label,
            loggable.longitude,
            loggable.latitude,
            loggable.loggableType
        )

        val createLoggableCmd = CreateLoggableCmd(
            loggable.label,
            loggable.longitude,
            loggable.latitude,
            loggable.loggableType
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq("create warehouse", CompanyOne.owner, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("create warehouse with admin", CompanyOne.admin, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq(
                "create warehouse in wrong company",
                CompanyTwo.owner,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),
            ParameterizedReq(
                "create warehouse with unauthorized user",
                CompanyOne.worker,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),
            // READ
            ParameterizedReq("reads warehouse", CompanyOne.owner, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq("reads warehouse", CompanyOne.admin, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq("reads warehouse", CompanyOne.worker, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq(
                "reads one from wrong company",
                CompanyTwo.worker,
                403,
                Method.READ,
                warehouse.id.toString()
            ),
            // READ ALL
            ParameterizedReq("reads all warehouses with admin", CompanyOne.admin, 200, Method.READ, null),
            ParameterizedReq("reads all warehouses with owner", CompanyOne.owner, 200, Method.READ, null),
            ParameterizedReq("reads all warehouses with stranger", CompanyOne.worker, 200, Method.READ, null),
            // UPDATE
            ParameterizedReq("update warehouse", CompanyOne.owner, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update warehouse", CompanyOne.admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq(
                "update warehouse from wrong company",
                CompanyTwo.owner,
                403,
                Method.UPDATE,
                Json.encodeToString(updateCmd)
            ),
            ParameterizedReq(
                "update warehouse with unauthorized user",
                CompanyOne.worker,
                403,
                Method.UPDATE,
                Json.encodeToString(updateCmd)
            ),
            /// DELETE
            ParameterizedReq("delete warehouse", CompanyOne.owner, 204, Method.DELETE, warehouse.id.toString()),
            ParameterizedReq(
                "delete warehouse from wrong company",
                CompanyTwo.owner,
                403,
                Method.DELETE,
                warehouse.id.toString()
            ),
            ParameterizedReq("delete warehouse", CompanyOne.admin, 204, Method.DELETE, warehouse.id.toString()),
            ParameterizedReq("delete warehouse", CompanyOne.worker, 403, Method.DELETE, warehouse.id.toString()),
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(
        pr: ParameterizedReq
    ) = configuredTestApplication {
        dependencies.provide<WarehouseRepository> { warehouseRepository }
        dependencies.provide<UserRepository> { usersRepository }
        dependencies.provide<LoggableRepository> { loggableRepository }
        every { warehouseRepository.addWarehouse(any()) } returns warehouse
        every { warehouseRepository.allWarehouses() } returns listOf(warehouse)
        every { warehouseRepository.warehouseById(warehouse.id) } returns warehouse
        every { warehouseRepository.updateWarehouse(warehouse.id, updateCmd) } returns warehouse
        every { warehouseRepository.removeWarehouse(warehouse.id) } returns warehouse
        every { loggableRepository.loggableById(loggable.id) } returns loggable
        every { loggableRepository.updateLoggable(loggable.id, updateLoggableCmd) } returns loggable
        every { loggableRepository.addLoggable(createLoggableCmd) } returns loggable
        every { loggableRepository.removeLoggable(loggable.id) } returns loggable
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("warehouse", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}