package fan.zheyuan.routes

import io.ktor.application.call
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.thymeleaf.ThymeleafContent

fun Route.index() {
    get<Index> {
        call.respond(ThymeleafContent("index", emptyMap()))
    }
}
@Location("/")
class Index()