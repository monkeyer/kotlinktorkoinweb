package fan.zheyuan.routes

import fan.zheyuan.domain.model.Beer
import fan.zheyuan.service.BeerService
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import org.koin.ktor.ext.inject

fun Route.beersRoute() {
    val beerService by inject<BeerService>()

    get("api/beers") {
        call.respond(beerService.getAllBeers())
    }

    post("api/beers") {
        val beer = call.receive<Beer>()
        val id = beerService.createBeer(beer)
        if (id != null) {
            call.respond(HttpStatusCode.Created, id)
        } else {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}