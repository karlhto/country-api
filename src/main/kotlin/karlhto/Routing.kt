package karlhto

import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/countries") {
            call.respondText("Hello World!")
        }
        get("/currencies") {
            call.respondText("Hello World!")
        }
    }
}
