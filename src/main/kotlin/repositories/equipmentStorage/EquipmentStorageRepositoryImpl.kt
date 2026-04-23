package at.eventful.messless.repositories.equipmentStorage

import at.eventful.messless.repositories.equipmentStorage.commands.CreateEquipmentStorageCmd
import at.eventful.messless.repositories.equipmentStorage.commands.UpdateEquipmentStorageCmd
import at.eventful.messless.repositories.loggable.LoggableRepositoryImpl
import at.eventful.messless.repositories.loggable.command.CreateLoggableCmd
import at.eventful.messless.repositories.loggable.command.UpdateLoggableCmd
import at.eventful.messless.schema.dao.EquipmentStorageDao
import at.eventful.messless.schema.entities.EquipmentStorageEntity
import at.eventful.messless.schema.entities.LoggableEntity
import at.eventful.messless.schema.tables.EquipmentStorageTable
import at.eventful.messless.schema.tables.WarehouseTable
import at.eventful.messless.schema.utils.LoggableType
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import kotlin.time.ExperimentalTime

class EquipmentStorageRepositoryImpl : EquipmentStorageRepository {
    val loggableRepository = LoggableRepositoryImpl()

    override fun addEquipmentStorage(equipmentStorage: CreateEquipmentStorageCmd): EquipmentStorageDao = transaction {
        EquipmentStorageDao.from(
            EquipmentStorageEntity.new {
                loggable = LoggableEntity.findById(
                    loggableRepository.addLoggable(
                        CreateLoggableCmd(
                            equipmentStorage.label,
                            equipmentStorage.longitude,
                            equipmentStorage.latitude,
                            LoggableType.Equipment,
                            equipmentStorage.companyId
                        )
                    ).id
                ) ?: throw Error("Loggable not found")
            }
        )!!
    }

    @OptIn(ExperimentalTime::class)
    override fun allEquipmentStorage(): List<EquipmentStorageDao> = transaction {
        EquipmentStorageEntity.find { EquipmentStorageTable.deletedAt.isNull() }.toList()
            .mapNotNull { EquipmentStorageDao.from(it) }
    }

    @OptIn(ExperimentalTime::class)
    override fun equipmentStorageById(id: Int): EquipmentStorageDao? = transaction {
        val equipmentStorage = EquipmentStorageEntity.findById(id)
        return@transaction if (equipmentStorage?.deletedAt == null) EquipmentStorageDao.from(equipmentStorage) else null
    }

    @OptIn(ExperimentalTime::class)
    override fun updateEquipmentStorage(
        id: Int,
        updateEquipmentStorageCmd: UpdateEquipmentStorageCmd
    ): EquipmentStorageDao = transaction {
        EquipmentStorageDao.from(EquipmentStorageEntity.findByIdAndUpdate(updateEquipmentStorageCmd.`$id`) {
            loggableRepository.updateLoggable(
                it.loggable.id.value,
                UpdateLoggableCmd(
                    it.loggable.id.value,
                    updateEquipmentStorageCmd.label,
                    updateEquipmentStorageCmd.longitude,
                    updateEquipmentStorageCmd.latitude,
                    LoggableType.Equipment,
                    updateEquipmentStorageCmd.companyId
                )
            )
        })!!
    }

    @OptIn(ExperimentalTime::class)
    override fun removeEquipmentStorage(id: Int): EquipmentStorageDao? = transaction {
        EquipmentStorageDao.from(EquipmentStorageEntity.findSingleByAndUpdate(EquipmentStorageTable.id eq id and WarehouseTable.deletedAt.isNull()) {
            loggableRepository.removeLoggable(it.loggable.id.value)
            it.deletedAt = null
        })
    }
}