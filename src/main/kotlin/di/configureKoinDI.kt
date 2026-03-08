package at.eventful.messless.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoinDI() {
    install(Koin) {
        slf4jLogger()
        modules(configureDependencyInjection())
    }
}