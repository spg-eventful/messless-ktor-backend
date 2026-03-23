package testutils

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import repositories.users.UserRepository
import testutils.AuthorizationTestCompanion.CompanyOne
import testutils.AuthorizationTestCompanion.CompanyTwo

abstract class AuthorizationTest {
    open val usersRepository = mockk<UserRepository>()

    companion object : AuthorizationTestCompanion()

    fun mockAuthRelatedMethods() {
        every { usersRepository.userById(CompanyOne.admin.id) } returns CompanyOne.admin
        every { usersRepository.userById(CompanyOne.owner.id) } returns CompanyOne.owner
        every { usersRepository.userById(CompanyOne.worker.id) } returns CompanyOne.worker
        every { usersRepository.userById(CompanyOne.stageHand.id) } returns CompanyOne.stageHand

        every { usersRepository.userById(CompanyTwo.admin.id) } returns CompanyTwo.admin
        every { usersRepository.userById(CompanyTwo.owner.id) } returns CompanyTwo.owner
        every { usersRepository.userById(CompanyTwo.worker.id) } returns CompanyTwo.worker
        every { usersRepository.userById(CompanyTwo.stageHand.id) } returns CompanyTwo.stageHand
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("requestMatrix")
    abstract fun makeRequest(
        pr: ParameterizedReq
    )
}