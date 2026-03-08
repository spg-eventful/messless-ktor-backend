package at.eventful.messless.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import repositories.users.UsersRepository

fun configureDependencyInjection(): Module {
    return module {
        singleOf(::UsersRepository)
    }
}