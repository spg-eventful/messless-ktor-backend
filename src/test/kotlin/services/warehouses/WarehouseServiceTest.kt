package services.warehouses

import ParameterizedReq
import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dao.CompanyDao
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
import testutils.AuthorizationTest
import testutils.configuredTestApplication
import testutils.sendAndAssert
import testutils.sendLoginFrame

@ExtendWith(MockKExtension::class)
class WarehouseServiceTest {
    val warehouseRepository = mockk<WarehouseRepository>()
    val userRepository = mockk<UserRepository>()

    companion object : AuthorizationTest() {
        val warehouse = WarehouseDao.fake(1)

        val createCmd = CreateWarehouseCmd(
            warehouse.label,
            warehouse.latitude,
            warehouse.longitude,
            owner.company?.id ?: 1,
        )

        val updateCmd = UpdateWarehouseCmd(
            warehouse.id,
            warehouse.label,
            warehouse.latitude,
            warehouse.longitude,
            owner.company?.id ?: 1,
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            //create
            ParameterizedReq("create warehouse", owner, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("create warehouse with admin", admin, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq(
                "create warehouse in wrong company",
                owner,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd.copy(companyId = 2))
            ),
            ParameterizedReq(
                "create warehouse with unauthorized user",
                worker,
                403,
                Method.CREATE,
                Json.encodeToString(createCmd)
            ),

            //update
            ParameterizedReq("update warehouse", owner, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update warehouse", admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq(
                "update warehouse from wrong company",
                owner,
                403,
                Method.UPDATE,
                Json.encodeToString(updateCmd.copy(companyId = 2))
            ),
            ParameterizedReq(
                "update warehouse with unauthorized user",
                worker,
                403,
                Method.UPDATE,
                Json.encodeToString(updateCmd)
            ),

            //find all
            ParameterizedReq("find all warehouses with admin", admin, 200, Method.READ, null),
            ParameterizedReq("find all warehouses with owner", owner, 200, Method.READ, null),
            ParameterizedReq("find all warehouses with stranger", worker, 200, Method.READ, null),

            //find one
            ParameterizedReq("find warehouse", owner, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq("find warehouse", admin, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq("find warehouse", worker, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq(
                "find one from wrong company",
                stranger.copy(company = CompanyDao.fake(2)),
                403,
                Method.READ,
                warehouse.id.toString()
            ),


            ///delete
            ParameterizedReq("delete warehouse", owner, 204, Method.DELETE, warehouse.id.toString()),
            ParameterizedReq(
                "delete warehouse",
                strangerOwner,
                403,
                Method.DELETE,
                warehouse.id.toString()
            ),
            ParameterizedReq("delete warehouse", admin, 204, Method.DELETE, warehouse.id.toString()),
            ParameterizedReq("delete warehouse", worker, 403, Method.DELETE, warehouse.id.toString()),
        )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    fun makeRequest(
        pr: ParameterizedReq
    ) = configuredTestApplication {
        dependencies.provide<WarehouseRepository> { warehouseRepository }
        dependencies.provide<UserRepository> { userRepository }
        every { userRepository.userById(admin.id) } returns admin
        every { userRepository.userById(owner.id) } returns owner
        every { userRepository.userById(strangerOwner.id) } returns strangerOwner
        every { userRepository.userById(stranger.id) } returns stranger
        every { userRepository.userById(worker.id) } returns worker
        every { warehouseRepository.addWarehouse(any()) } returns warehouse
        every { warehouseRepository.allWarehouses() } returns listOf(warehouse)
        every { warehouseRepository.warehouseById(warehouse.id) } returns warehouse
        every { warehouseRepository.updateWarehouse(warehouse.id, updateCmd) } returns warehouse
        every { warehouseRepository.removeWarehouse(warehouse.id) } returns warehouse

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("warehouse", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}