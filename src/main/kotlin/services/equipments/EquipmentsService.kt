package at.eventful.messless.services.equipments

import at.eventful.messless.errors.responses.Forbidden
import at.eventful.messless.errors.responses.NotFound
import at.eventful.messless.errors.responses.Unauthorized
import at.eventful.messless.plugins.socket.ServiceMethod
import at.eventful.messless.plugins.socket.WebSocketService
import at.eventful.messless.plugins.socket.model.WebSocketResponse
import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.repositories.equipmentStorage.EquipmentStorageRepository
import at.eventful.messless.repositories.equipmentStorage.commands.CreateEquipmentStorageCmd
import at.eventful.messless.repositories.loggable.LoggableRepository
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.repositories.warehouse.WarehouseRepository
import at.eventful.messless.schema.dto.EquipmentDto
import at.eventful.messless.schema.utils.LoggableType
import at.eventful.messless.schema.utils.UserRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*

class EquipmentsService(app: Application) : WebSocketService("equipments") {
    val equipmentRepo: EquipmentRepository by app.dependencies
    val warehouseRepo: WarehouseRepository by app.dependencies
    val equipmentStorageRepo: EquipmentStorageRepository by app.dependencies
    val loggableRepo: LoggableRepository by app.dependencies

    override fun ServiceMethod.create(): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 2) throw Forbidden("You are not allowed to create equipment!")

            val cmd = incoming.receiveBody<CreateEquipmentCmd>()

            if (!cmd.isStorage) {
                val warehouse = warehouseRepo.warehouseById(cmd.belongsToWarehouse)
                    ?: throw NotFound("Warehouse with id ${cmd.belongsToWarehouse} not found")

                if (warehouse.company?.id != it.user.company?.id && it.user.role.asInt() != UserRole.Admin.asInt()) throw Forbidden(
                    "You are not allowed to create equipment in another companies warehouse!"
                )

                try {
                    return WebSocketResponse.from(
                        HttpStatusCode.Created,
                        EquipmentDto.from(
                            equipmentRepo.addEquipment(cmd), null
                        ),
                    )
                } catch (e: Exception) {
                    throw e
                }
            } else {
                val warehouse = warehouseRepo.warehouseById(cmd.belongsToWarehouse)
                    ?: throw NotFound("Warehouse with id ${cmd.belongsToWarehouse} not found")

                if (warehouse.company?.id != it.user.company?.id && it.user.role.asInt() != UserRole.Admin.asInt()) throw Forbidden(
                    "You are not allowed to create equipment in another companies warehouse!"
                )

                try {
                    val equipmentStorage = equipmentStorageRepo.addEquipmentStorage(
                        CreateEquipmentStorageCmd(
                            cmd.label,
                            warehouse.loggable?.latitude
                                ?: throw NotFound("Warehouse ${warehouse.id} has no location!"),
                            warehouse.loggable.longitude,
                            warehouse.company?.id ?: throw NotFound("Warehouse ${warehouse.id} has no company!")
                        )
                    )
                    val equipment = equipmentRepo.addEquipment(
                        CreateEquipmentCmd(
                            cmd.label,
                            cmd.belongsToWarehouse,
                            cmd.isStorage
                        )
                    )
                    return WebSocketResponse.from(
                        HttpStatusCode.Created,
                        EquipmentDto.from(
                            equipment,
                            loggableRepo.loggableById(
                                equipmentStorage.loggable?.id ?: throw NotFound("Loggable not found")
                            )
                        ),
                    )
                } catch (e: Exception) {
                    throw e
                }
            }
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.find(): WebSocketResponse<List<EquipmentDto>> {
        connection.auth.auth?.let {
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                equipmentRepo.allEquipment().map { equipment ->
                    EquipmentDto.from(
                        equipment,
                        if (equipment.storage == null) null else {
                            loggableRepo.loggableById(
                                equipmentStorageRepo.equipmentStorageById(
                                    equipment.storage
                                )?.loggable?.id
                                    ?: throw NotFound("Loggable not found")
                            ) ?: throw NotFound("Loggable not found")
                        }

                    )
                },
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.get(id: Int): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            val equipment = equipmentRepo.equipmentById(id) ?: throw NotFound("Equipment with id $id not found")
            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EquipmentDto.from(
                    equipment,
                    if (equipment.storage == null) null else {
                        loggableRepo.loggableById(
                            equipmentStorageRepo.equipmentStorageById(
                                equipment.storage
                            )?.loggable?.id ?: throw NotFound("Loggable not found")
                        )
                            ?: throw NotFound(
                                "Loggable not found"
                            )
                    }

                )
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.update(id: Int): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            val cmd = incoming.receiveBody<UpdateEquipmentCmd>()
            val updated = equipmentRepo.updateEquipment(id, cmd)
                ?: throw NotFound("Equipment with id $id not found")

            val lat = cmd.latitude
            val lon = cmd.longitude

            if (lat != null && lon != null) {
                val storage = equipmentStorageRepo.equipmentStorageById(
                    updated.storage ?: throw NotFound("Equipment is not a storage")
                )
                    ?: throw NotFound("EquipmentStorage not found")
                val loggableId = storage.loggable?.id ?: throw NotFound("Loggable not found")
                val warehouse =
                    warehouseRepo.warehouseById(updated.belongsToWarehouse) ?: throw NotFound("Warehouse not found")
                loggableRepo.updateLoggable(
                    loggableId,
                    UpdateLoggableCmd(
                        loggableId,
                        updated.label,
                        lon,
                        lat,
                        LoggableType.Equipment,
                        warehouse.company?.id ?: throw NotFound("Warehouse has no company!")
                    )
                )
            }

            return WebSocketResponse.from(
                HttpStatusCode.OK,
                EquipmentDto.from(
                    updated,
                    loggableRepo.loggableById(
                        equipmentStorageRepo.equipmentStorageById(updated.storage!!)?.loggable?.id
                            ?: throw NotFound("Loggable for equipment storage not found")
                    )
                ),
            )
        }
        throw Unauthorized()
    }

    override fun ServiceMethod.delete(id: Int): WebSocketResponse<EquipmentDto> {
        connection.auth.auth?.let {
            if (it.user.role.asInt() < 2) throw Forbidden("You are not allowed to delete equipment!")
            equipmentRepo.removeEquipment(id) ?: throw NotFound("Equipment with id $id not found")
            return WebSocketResponse(
                HttpStatusCode.NoContent
            )
        }
        throw Unauthorized()
    }
}