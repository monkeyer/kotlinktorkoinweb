package fan.zheyuan

import fan.zheyuan.applications.web
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testRoot() {
        withTestApplication({ web(true) }) {
            handleRequest(HttpMethod.Get, "/hello").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("hello", response.content)
            }
        }
    }
}
