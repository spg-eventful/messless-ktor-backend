package services.warehouses

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.warehouse.WarehouseRepository
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

    companion object : AuthorizationTestCompanion() {
        val warehouse = WarehouseDao.fake(1)

        val createCmd = CreateWarehouseCmd(
            warehouse.label,
            warehouse.latitude,
            warehouse.longitude,
            CompanyOne.owner.company?.id ?: 1,
        )

        val updateCmd = UpdateWarehouseCmd(
            warehouse.id,
            warehouse.label,
            warehouse.latitude,
            warehouse.longitude,
            CompanyOne.owner.company?.id ?: 1,
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            //create
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

            //update
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

            //find all
            ParameterizedReq("find all warehouses with admin", CompanyOne.admin, 200, Method.READ, null),
            ParameterizedReq("find all warehouses with owner", CompanyOne.owner, 200, Method.READ, null),
            ParameterizedReq("find all warehouses with stranger", CompanyOne.worker, 200, Method.READ, null),

            //find one
            ParameterizedReq("find warehouse", CompanyOne.owner, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq("find warehouse", CompanyOne.admin, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq("find warehouse", CompanyOne.worker, 200, Method.READ, warehouse.id.toString()),
            ParameterizedReq(
                "find one from wrong company",
                CompanyTwo.worker,
                403,
                Method.READ,
                warehouse.id.toString()
            ),


            ///delete
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
        every { warehouseRepository.addWarehouse(any()) } returns warehouse
        every { warehouseRepository.allWarehouses() } returns listOf(warehouse)
        every { warehouseRepository.warehouseById(warehouse.id) } returns warehouse
        every { warehouseRepository.updateWarehouse(warehouse.id, updateCmd) } returns warehouse
        every { warehouseRepository.removeWarehouse(warehouse.id) } returns warehouse
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("warehouse", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}