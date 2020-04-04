package fan.zheyuan.routes

import io.ktor.http.content.*
import io.ktor.routing.Route

fun Route.routeFilesystem() {
    static("/") {
        resources("files")
    }
}
