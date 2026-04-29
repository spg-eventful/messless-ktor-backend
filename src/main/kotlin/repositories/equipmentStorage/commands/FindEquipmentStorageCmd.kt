package at.eventful.messless.repositories.equipmentStorage.commands

import kotlinx.serialization.Serializable

@Serializable
data class FindEquipmentStorageCmd(val isEquipmentStorage: Boolean? = false) {
}