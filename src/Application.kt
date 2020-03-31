package fan.zheyuan

import com.fasterxml.jackson.databind.DeserializationFeature
import fan.zheyuan.routes.login
import fan.zheyuan.routes.styles
import fan.zheyuan.routes.upload
import fan.zheyuan.routes.videos
import io.ktor.application.Application
import io.ktor.application.install
import io.ktor.auth.UserHashedTableAuth
import io.ktor.features.*
import io.ktor.gson.gson
import io.ktor.http.ContentType
import io.ktor.jackson.jackson
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.routing.routing
import io.ktor.sessions.SessionTransportTransformerMessageAuthentication
import io.ktor.sessions.Sessions
import io.ktor.sessions.cookie
import io.ktor.util.getDigestFunction
import io.ktor.util.hex
import java.io.File
import java.io.IOException
import java.util.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {

    install(DefaultHeaders)
    install(Locations)
    install(CallLogging)
    install(ConditionalHeaders)
    install(PartialContent)
    install(Compression) {
        default()
        excludeContentType(ContentType.Video.Any)
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
            disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

    routing {

        login(users)
        upload(database, uploadDir)
        videos(database)
        styles()
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