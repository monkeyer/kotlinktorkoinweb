package fan.zheyuan.configuration

import fan.zheyuan.routes.index
import fan.zheyuan.routes.routeFilesystem
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.thymeleaf.ThymeleafContent

fun Application.route() {

    routing {
        routeFilesystem()
        index()
        get("upload") {
            call.respond(ThymeleafContent("upload", emptyMap()))
        }
        get("/hello") {
            call.respondText("hello")
        }
    }
}