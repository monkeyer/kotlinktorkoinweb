package fan.zheyuan.ktorkoin

import fan.zheyuan.applications.beerqlModule
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import org.koin.core.context.startKoin
import org.koin.ktor.ext.*
import org.koin.logger.slf4jLogger

//fun main(args: Array<String>) = io.ktor.server.netty.EngineMain.main(args)

fun Application.hello() {

    environment.monitor.subscribe(KoinApplicationStarted) {
        log.info("Koin started.")
    }
    /*install(Koin) {
        slf4jLogger()
        modules(helloAppModule)
    }*/

    startKoin {
        slf4jLogger()
        modules(helloAppModule, beerqlModule)
    }

    environment.monitor.subscribe(KoinApplicationStopPreparing) {
        log.info("Koin stopping...")
    }

    environment.monitor.subscribe(KoinApplicationStopped) {
        log.info("Koin stopped.")
    }

    val service by inject<HelloService>()

    routing {
        get("/hello") {
           call.respondText(service.sayHello())
        }
    }
}