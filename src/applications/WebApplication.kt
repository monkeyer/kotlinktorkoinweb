package fan.zheyuan.applications

import fan.zheyuan.configuration.appConfig
import fan.zheyuan.configuration.route
import io.ktor.application.Application

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.web(testing: Boolean = false) {
    appConfig()
    route()
}

