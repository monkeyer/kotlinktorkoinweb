package fan.zheyuan.routes

import io.ktor.application.call
import io.ktor.http.content.resolveResource
import io.ktor.locations.Location
import io.ktor.locations.get
import io.ktor.response.respond
import io.ktor.routing.Route

@Location("/css")
class MainCss()

fun Route.styles() {
    get<MainCss> {
        call.respond(call.resolveResource("blog.css")!!)

    }
}