package eu.yeger.apollo

import eu.yeger.apollo.routing.routingModule
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ApplicationTest {

  @Test
  fun testRoot() {
    withTestApplication({
      mainModule()
      routingModule()
    }) {
      handleRequest(HttpMethod.Get, "/").apply {
        assertEquals(HttpStatusCode.OK, response.status())
        assertEquals("Apollo-Backend is available!", response.content)
      }
    }
  }
}
