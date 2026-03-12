package at.eventful.messless.repositories.equipment

import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import at.eventful.messless.repositories.equipment.commands.UpdateEquipmentCmd
import at.eventful.messless.schema.dao.EquipmentDao

interface EquipmentRepository {
    fun addEquipment(equipment: CreateEquipmentCmd): EquipmentDao
    fun allEquipment(): List<EquipmentDao>
    fun equipmentById(id: Int): EquipmentDao?
    fun updateEquipment(id: Int, equipment: UpdateEquipmentCmd): EquipmentDao?
    fun removeEquipment(id: Int): EquipmentDao?
}