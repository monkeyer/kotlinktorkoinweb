package fan.zheyuan.routes

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fan.zheyuan.graphql.GraphQLModel
import io.ktor.application.call
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.respondText
import io.ktor.routing.Route
import io.ktor.routing.post
import org.koin.ktor.ext.inject

data class GraphQlRequest(val query: String = "", val variables: Map<String, Any>? = emptyMap())

fun Route.graphQLRoute() {
    val graphQLModel by inject<GraphQLModel>()

    post("/beerql") {
        val req = call.receive<GraphQlRequest>()
        println("req is $req")
        val variables = if (req.variables == null) null
        else jacksonObjectMapper().writeValueAsString(req.variables)
        var result = ""
        try {
            result = graphQLModel.schema.execute(req.query, variables)
        }catch (e: Exception) {
            call.respondText("""{"error": "${e.message}""".trimIndent(),
                ContentType.Application.Json,
                HttpStatusCode.InternalServerError)
        }

        call.respondText(result, ContentType.Application.Json, HttpStatusCode.Created)
    }
}