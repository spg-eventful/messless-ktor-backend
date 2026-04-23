package at.eventful.messless.repositories.equipment

import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.repositories.warehouse.WarehouseRepositoryImpl
import at.eventful.messless.schema.dao.EquipmentDao
import at.eventful.messless.schema.entities.EquipmentEntity
import at.eventful.messless.schema.entities.EquipmentStorageEntity
import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.entities.WarehouseEntity
import at.eventful.messless.schema.tables.EquipmentTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EquipmentRepositoryImpl : EquipmentRepository {
    val warehouseRepository = WarehouseRepositoryImpl()

    override fun addEquipment(equipment: CreateEquipmentCmd): EquipmentDao = transaction {
        EquipmentDao.from(EquipmentEntity.new {
            label = equipment.label
            belongsTo = WarehouseEntity.findById(equipment.belongsToWarehouse) ?: throw Error("Warehouse not found")
            if (equipment.isStorage) {
                isStorage = EquipmentStorageEntity.new {
                    loggable = LoggableEntity.findById(
                        warehouseRepository.warehouseById(equipment.belongsToWarehouse)?.loggable?.id
                            ?: throw Error("No Loggable Id on Warehouse")
                    ) ?: throw Error("Loggable not found")
                }
            }
            isStorage = null
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allEquipment(): List<EquipmentDao> = transaction {
        EquipmentEntity.find { (EquipmentTable.deletedAt eq null) }.toList()
            .mapNotNull { EquipmentDao.from(it) }
    }

    @OptIn(ExperimentalTime::class)
    override fun equipmentById(id: Int): EquipmentDao? = transaction {
        val equipment = EquipmentEntity.findById(id)
        return@transaction if (equipment?.deletedAt == null) EquipmentDao.from(equipment) else null
    }

    override fun updateEquipment(id: Int, equipment: UpdateEquipmentCmd): EquipmentDao? = transaction {
        EquipmentDao.from(EquipmentEntity.findByIdAndUpdate(id) {
            it.label = equipment.label
            it.belongsTo = WarehouseEntity.findById(equipment.belongsToWarehouse) ?: throw Error("Warehouse not found")
            it.isStorage = equipment.equipmentStorage?.let {
                EquipmentStorageEntity.findById(it) ?: throw Error("Equipment storage not found")
            }
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeEquipment(id: Int): EquipmentDao? = transaction {
        EquipmentDao.from(EquipmentEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }

}