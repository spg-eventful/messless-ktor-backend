package at.eventful.messless.services.index

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.registerIndexRoute() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "OK")
        }
    }
}