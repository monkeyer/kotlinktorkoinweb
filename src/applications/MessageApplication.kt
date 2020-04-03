package fan.zheyuan.applications

import com.viartemev.ktor.flyway.FlywayFeature
import fan.zheyuan.exception.BaseHttpException
import fan.zheyuan.routes.message
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.routing
import org.koin.ktor.ext.inject
import java.time.ZonedDateTime
import javax.sql.DataSource

/*
fun Application.message() {
    val data by inject<DataSource>()



    install(FlywayFeature) {
        dataSource = data
    }
    routing { message() }
}*/
