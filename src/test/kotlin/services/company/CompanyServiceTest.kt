package services.company

import at.eventful.messless.plugins.socket.model.Method
import at.eventful.messless.repositories.company.CompanyRepository
import at.eventful.messless.repositories.company.commands.CreateCompanyCmd
import at.eventful.messless.repositories.company.commands.UpdateCompanyCmd
import at.eventful.messless.schema.dao.CompanyDao
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
class CompanyServiceTest : AuthorizationTest() {
    val companyRepository = mockk<CompanyRepository>()
    override val usersRepository = mockk<UserRepository>()

    companion object : AuthorizationTestCompanion() {
        val company = CompanyDao.fake(1)

        val updateCmd = UpdateCompanyCmd(
            company.id,
            company.label,
            company.longitude,
            company.latitude,
        )

        val createCmd = CreateCompanyCmd(
            company.label,
            company.longitude,
            company.latitude,
        )

        @JvmStatic
        fun requestMatrix() = listOf(
            // CREATE
            ParameterizedReq("create company", CompanyOne.admin, 201, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("create company", CompanyOne.owner, 403, Method.CREATE, Json.encodeToString(createCmd)),
            ParameterizedReq("create company", CompanyOne.worker, 403, Method.CREATE, Json.encodeToString(createCmd)),

            // READ
            ParameterizedReq("reads company", CompanyOne.admin, 200, Method.READ, company.id.toString()),
            ParameterizedReq("reads company", CompanyOne.owner, 200, Method.READ, company.id.toString()),
            ParameterizedReq("reads company", CompanyOne.worker, 200, Method.READ, company.id.toString()),
            ParameterizedReq("reads company", CompanyTwo.worker, 403, Method.READ, company.id.toString()),

            // READ ALL
            ParameterizedReq("reads all companies", CompanyOne.admin, 200, Method.READ, null),
            ParameterizedReq("reads all companies", CompanyOne.owner, 403, Method.READ, null),
            ParameterizedReq("reads all companies", CompanyOne.worker, 403, Method.READ, null),

            // UPDATE
            ParameterizedReq("update company", CompanyOne.admin, 200, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update company", CompanyOne.owner, 403, Method.UPDATE, Json.encodeToString(updateCmd)),
            ParameterizedReq("update company", CompanyOne.worker, 403, Method.UPDATE, Json.encodeToString(updateCmd)),

            // DELETE
            ParameterizedReq("delete company", CompanyOne.admin, 204, Method.DELETE, company.id.toString()),
            ParameterizedReq("delete company", CompanyOne.owner, 403, Method.DELETE, company.id.toString()),
            ParameterizedReq("delete company", CompanyOne.worker, 403, Method.DELETE, company.id.toString()),

            )
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    override fun makeRequest(pr: ParameterizedReq) = configuredTestApplication {
        dependencies.provide<CompanyRepository> { companyRepository }
        dependencies.provide<UserRepository> { usersRepository }
        every { companyRepository.allCompanies() } returns listOf(company)
        every { companyRepository.addCompany(any()) } returns company
        every { companyRepository.updateCompany(company.id, updateCmd) } returns company
        every { companyRepository.removeCompany(company.id) } returns company
        every { companyRepository.companyById(company.id) } returns company
        mockAuthRelatedMethods()

        client.webSocket("/ws") {
            run {
                sendLoginFrame(this@configuredTestApplication, pr.user)
                sendAndAssert("companies", pr.method, pr.payload, pr.expectedStatus)
            }
        }
    }
}