package at.eventful.messless.repositories.equipment

import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.schema.dao.EquipmentDao
import at.eventful.messless.schema.entities.EquipmentEntity
import at.eventful.messless.schema.tables.EquipmentTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class EquipmentRepositoryImpl : EquipmentRepository {
    override fun addEquipment(equipment: CreateEquipmentCmd): EquipmentDao = transaction {
        EquipmentDao.from(EquipmentEntity.new {
            label = equipment.label
            location = equipment.location
            belongsTo = equipment.belongsTo
            storage = equipment.storage
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allEquipment(): List<EquipmentDao> = transaction {
        EquipmentEntity.find { (EquipmentTable.deletedAt eq null) }.toList()
            .map { EquipmentDao::from } as List<EquipmentDao>
    }

    @OptIn(ExperimentalTime::class)
    override fun equipmentById(id: Int): EquipmentDao? = transaction {
        val equipment = EquipmentEntity.findById(id)
        return@transaction if (equipment?.deletedAt == null) EquipmentDao.from(equipment) else null
    }

    override fun updateEquipment(id: Int, equipment: UpdateEquipmentCmd): EquipmentDao? = transaction {
        EquipmentDao.from(EquipmentEntity.findByIdAndUpdate(id) {
            it.label = equipment.label
            it.location = equipment.location
            it.belongsTo = equipment.belongsTo
            it.storage = equipment.storage
        })
    }

    @OptIn(ExperimentalTime::class)
    override fun removeEquipment(id: Int): EquipmentDao? = transaction {
        EquipmentDao.from(EquipmentEntity.findByIdAndUpdate(id) {
            it.deletedAt = Clock.System.now()
        })
    }

}