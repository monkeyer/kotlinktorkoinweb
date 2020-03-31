package fan.zheyuan.applications

import com.fasterxml.jackson.databind.DeserializationFeature
import fan.zheyuan.domain.repository.BeerRepository
import fan.zheyuan.graphql.GraphQLModel
import fan.zheyuan.routes.beersRoute
import fan.zheyuan.routes.graphQLRoute
import fan.zheyuan.service.BeerService
import io.ktor.application.Application
import io.ktor.application.ApplicationStarted
import io.ktor.application.install
import io.ktor.features.CORS
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.routing.routing
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.ktor.ext.koin
import org.koin.ktor.ext.modules
import org.koin.logger.slf4jLogger

val beerqlModule = module {
    factory { BeerRepository() }
    factory { BeerService(get()) }
    single { GraphQLModel(get()) }
}

fun Application.beerql() {

    environment.monitor.subscribe(ApplicationStarted) {
        val beerService by inject<BeerService>()
        beerService.importBeers()
    }

    install(CORS) {
        allowSameOrigin
        anyHost()
    }

    routing {
        graphQLRoute()
        beersRoute()
    }
}