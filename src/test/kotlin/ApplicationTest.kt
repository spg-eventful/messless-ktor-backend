import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import testutils.configuredTestApplication
import kotlin.test.Test
import kotlin.test.assertEquals

class ApplicationTest {
    @Test
    fun testApplicationIndex() = configuredTestApplication {
        val response = client.get("/")
        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("OK", response.bodyAsText())
    }
}