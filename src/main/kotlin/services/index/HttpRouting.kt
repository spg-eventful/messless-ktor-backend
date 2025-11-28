package at.eventful.messless.services.index

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.registerIndexRoute() {
    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "OK")
        }
    }
}