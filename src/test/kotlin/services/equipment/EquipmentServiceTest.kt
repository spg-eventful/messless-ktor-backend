package services.equipment

import at.eventful.messless.repositories.equipment.EquipmentRepository
import at.eventful.messless.repositories.equipment.commands.CreateEquipmentCmd
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class EquipmentServiceTest {
    val equipmentRepository = mockk<EquipmentRepository>()

    fun equipmentFakeCreateCmd(): CreateEquipmentCmd = CreateEquipmentCmd(
        "Fender Champion II",
    )
}