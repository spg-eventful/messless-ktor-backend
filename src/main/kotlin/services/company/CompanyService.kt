package at.eventful.messless.services.company

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.company.CompanyRepository
import at.eventful.messless.repositories.company.commands.CreateCompanyCmd
import at.eventful.messless.repositories.company.commands.UpdateCompanyCmd
import at.eventful.messless.schema.dto.CompanyDto
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class CompanyService(app: Application) : WebSocketService("companies") {
    val companyRepo: CompanyRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<CompanyDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() != 5) throw Forbidden("You are not allowed to create a company!")

            val cmd = incoming.receiveBody<CreateCompanyCmd>()

            try {
                return WebSocketResponse.from(
                    HttpStatusCode.Created,
                    CompanyDto.from(companyRepo.addCompany(cmd))
                )
            } catch (e: Exception) {
                throw e
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<CompanyDto>> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() != 5) throw Forbidden("You are not allowed to query for companies!")

            return WebSocketResponse.from(
                HttpStatusCode.OK,
                companyRepo.allCompanies().map(CompanyDto::from)
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<CompanyDto> {
        connection.auth.auth?.let {
            if (it.user.company?.id != id) throw Forbidden("You are not allowed to query for companies that are not yours!")

            val company = companyRepo.companyById(id) ?: throw NotFound("Company with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                CompanyDto.from(company)
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<CompanyDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() != 5) throw Forbidden("You are not allowed to update a company!")

            val updated = companyRepo.updateCompany(id, incoming.receiveBody<UpdateCompanyCmd>())
                ?: throw NotFound("Company with id $id not found")

            return WebSocketResponse.from(
                HttpStatusCode.OK,
                CompanyDto.from(updated)
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<CompanyDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() != 5) throw Forbidden("You are not allowed to delete a company!")
            companyRepo.removeCompany(id) ?: throw NotFound("Company with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}