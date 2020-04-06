package fan.zheyuan

import fan.zheyuan.applications.web
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.junit.Test
import org.koin.test.AutoCloseKoinTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class ApplicationJobRoutesTest : AutoCloseKoinTest() {

    @Test
    fun testHelloRequest() = withTestApplication({web(true)}) {
        with(handleRequest(HttpMethod.Get, "/hello")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("hello", response.content)
        }

        with(handleRequest(HttpMethod.Get, "index.html")) {
            assertFalse(requestHandled)
        }
    }
}