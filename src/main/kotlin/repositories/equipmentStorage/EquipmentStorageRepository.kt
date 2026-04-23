package at.eventful.messless.repositories.equipmentStorage

import at.eventful.messless.repositories.equipmentStorage.commands.CreateEquipmentStorageCmd
import at.eventful.messless.repositories.equipmentStorage.commands.UpdateEquipmentStorageCmd
import at.eventful.messless.schema.dao.EquipmentStorageDao

interface EquipmentStorageRepository {
    fun addEquipmentStorage(equipmentStorage: CreateEquipmentStorageCmd): EquipmentStorageDao
    fun allEquipmentStorage(): List<EquipmentStorageDao>
    fun equipmentStorageById(id: Int): EquipmentStorageDao?
    fun updateEquipmentStorage(id: Int, equipmentStorage: UpdateEquipmentStorageCmd): EquipmentStorageDao?
    fun removeEquipmentStorage(id: Int): EquipmentStorageDao?
}