package fan.zheyuan.configuration

import fan.zheyuan.routes.routeFilesystem
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.jackson.JacksonConverter
import io.ktor.jackson.jackson
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.thymeleaf.Thymeleaf
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.appConfig() {

    install(DefaultHeaders)
    install(Locations)
    install(CallLogging)
    install(ConditionalHeaders)
    install(PartialContent)
    install(Compression) {
        default()
        excludeContentType(ContentType.Video.Any)
    }
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = "utf-8"
        })
    }
    install(ContentNegotiation) {
        gson {
        }
        jackson {
            register(ContentType.Application.Json, JacksonConverter(appJacksonMapper))
        }
    }
}