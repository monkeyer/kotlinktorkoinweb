package fan.zheyuan.routes

import fan.zheyuan.*
import fan.zheyuan.extends.respondDefaultHtml
import fan.zheyuan.extends.respondRedirect
import io.ktor.application.call
import io.ktor.http.CacheControl
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.locations.get
import io.ktor.locations.post
import io.ktor.locations.url
import io.ktor.request.receiveMultipart
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.sessions.get
import io.ktor.sessions.sessions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import kotlinx.html.*
import java.io.File
import java.io.InputStream
import java.io.OutputStream

fun Route.upload(database: Database, uploadDir: File) {
    get<Upload> {
        val session = call.sessions.get<YouKubeSession>()
        if (session == null) {
            call.respondRedirect(Login())
        } else {
            call.respondDefaultHtml(emptyList(), CacheControl.Visibility.Private) {
                h2 { +"Upload video" }

                form(
                    call.url(Upload()),
                    classes = "pure-form-stacked",
                    encType = FormEncType.multipartFormData,
                    method = FormMethod.post
                ) {
                    acceptCharset = "utf-8"

                    label {
                        htmlFor = "title"; +"Title:"
                        textInput { name = "title"; id = "title" }
                    }

                    br()
                    fileInput { name = "file" }
                    br()

                    submitInput(classes = "pure-button pure-button-primary") { value = "Upload" }
                }
            }
        }
    }

    /**
     * Registers a POST route for [Upload] that actually read the bits sent from the client and creates a new video
     * using the [database] and the [uploadDir].
     */
    post<Upload> {
        val session = call.sessions.get<YouKubeSession>()
        if (session == null) {
            call.respond(HttpStatusCode.Forbidden.description("Not logged in"))
        } else {
            val multipart = call.receiveMultipart()
            var title = ""
            var videoFile: File? = null

            // Processes each part of the multipart input content of the user
            multipart.forEachPart { part ->
                if (part is PartData.FormItem) {
                    if (part.name == "title") {
                        title = part.value
                    }
                } else if (part is PartData.FileItem) {
                    val ext = File(part.originalFileName).extension
                    val file = File(
                        uploadDir,
                        "upload-${System.currentTimeMillis()}-${session.userId.hashCode()}-${title.hashCode()}.$ext"
                    )

                    part.streamProvider().use { its -> file.outputStream().buffered().use { its.copyToSuspend(it) } }
                    videoFile = file
                }

                part.dispose()
            }

            val id = database.addVideo(title, session.userId, videoFile!!)

            call.respondRedirect(VideoPage(id))
        }
    }
}

/**
 * Utility boilerplate method that suspending,
 * copies a [this] [InputStream] into an [out] [OutputStream] in a separate thread.
 *
 * [bufferSize] and [yieldSize] allows to control how and when the suspending is performed.
 * The [dispatcher] allows to specify where will be this executed (for example a specific thread pool).
 */
suspend fun InputStream.copyToSuspend(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    yieldSize: Int = 4 * 1024 * 1024,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
): Long {
    return withContext(dispatcher) {
        val buffer = ByteArray(bufferSize)
        var bytesCopied = 0L
        var bytesAfterYield = 0L
        while (true) {
            val bytes = read(buffer).takeIf { it >= 0 } ?: break
            out.write(buffer, 0, bytes)
            if (bytesAfterYield >= yieldSize) {
                yield()
                bytesAfterYield %= yieldSize
            }
            bytesCopied += bytes
            bytesAfterYield += bytes
        }
        return@withContext bytesCopied
    }
}
