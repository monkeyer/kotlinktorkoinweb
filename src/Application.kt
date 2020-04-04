package fan.zheyuan

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.viartemev.ktor.flyway.FlywayFeature
import fan.zheyuan.applications.beerqlModule
import fan.zheyuan.configuration.appJacksonMapper
import fan.zheyuan.di.messageModule
import fan.zheyuan.exception.BaseHttpException
import fan.zheyuan.ktorkoin.helloAppModule
import fan.zheyuan.routes.*
import fan.zheyuan.utils.DatabaseCheck
import fan.zheyuan.utils.HealthCheck
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.UserHashedTableAuth
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.JacksonConverter
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.thymeleaf.Thymeleaf
import io.ktor.util.getDigestFunction
import io.ktor.util.hex
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.File
import java.io.IOException
import java.time.ZonedDateTime
import java.util.*
import javax.sql.DataSource

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.modules(testing: Boolean = false) {

    val ktorModule = module { single { environment } }

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
    install(StatusPages) {
        exception<BaseHttpException> {
            call.respond(
                HttpStatusCode.fromValue(it.httpStatus),
                mapOf(
                    "status" to it.httpStatus,
                    "message" to (it.message ?: ""),
                    "timestamp" to ZonedDateTime.now().toString()
                )
            )
        }
    }

    startKoin {
        slf4jLogger()
//        modules(ktorModule, messageModule)
        modules(ktorModule, helloAppModule, beerqlModule, messageModule)
    }

    val data by inject<DataSource>()
    val databaseCheck by inject<DatabaseCheck>()

    install(HealthCheck) {
        check("database") { databaseCheck.doHealthCheck() }
    }
    val youkubeConfig = environment.config.config("youkube")
    val sessionCookieConfig = youkubeConfig.config("session.cookie")
    val key = sessionCookieConfig.property("key").getString()
    val sessionKey = hex(key)

    val uploadDirPath = youkubeConfig.property("upload.dir").getString()
    val uploadDir = File(uploadDirPath)

    if (!uploadDir.mkdirs() && !uploadDir.exists()) {
        throw IOException("Failed to create directory ${uploadDir.absolutePath}")
    }
    val database = Database(uploadDir)

    val users = UserHashedTableAuth(
        getDigestFunction("SHA-256") { "ktor${it.length}" },
        table = mapOf(
            "root" to Base64.getDecoder().decode("76pc9N9hspQqapj30kCaLJA14O/50ptCg50zCA1oxjA=")
        )
    )

    install(Sessions) {
        cookie<YouKubeSession>("SESSION") {
            transform(SessionTransportTransformerMessageAuthentication(sessionKey))
        }
    }

    install(ContentNegotiation) {
        gson {
        }
        jackson {
            register(ContentType.Application.Json, JacksonConverter(appJacksonMapper))
        }
    }

    install(FlywayFeature) {
        dataSource = data
    }

    routing {

        login(users)
        upload(database, uploadDir)
        videos(database)
        styles()
        message()
    }
}

data class YouKubeSession(val userId: String)

@Location("/login")
data class Login(val userName: String = "", val password: String = "")

@Location("/")
class Index()

@Location("/upload")
class Upload()

@Location("/video/{id}")
data class VideoStream(val id: Long)

@Location("video/page/{id}")
data class VideoPage(val id: Long)
