package eu.yeger.apollo.routing

import eu.yeger.apollo.*
import eu.yeger.apollo.shared.model.api.Feedback
import eu.yeger.apollo.utils.shouldBe
import eu.yeger.apollo.utils.shouldNotBe
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*

class ModelCheckerRouteTests {

  @Test
  fun `verify that the route is configured properly`() {
    withTestApplication({
      mainModule()
      authModule()
      koinModule()
      routingModule()
      initializationModule()
    }) {
      runBlocking {
        (0..5).forEach { _ -> launch { makeCall("en", Feedback.Full) } }
        (0..5).forEach { _ -> launch { makeCall("de", Feedback.Relevant) } }
        (0..5).forEach { _ -> launch { makeCall("de", Feedback.Minimal) } }
      }
    }
  }

  private fun TestApplicationEngine.makeCall(language: String, feedback: Feedback) {
    handleRequest {
      method = HttpMethod.Post
      uri = "/model-checker"
      addHeader("Content-Type", "application/json")
      setBody(
        """
                    {
                        "formula": "exists x. exists y. B(x,y)",
                        "language": "$language",
                        "feedback": "${feedback.name.lowercase(Locale.getDefault())}",
                        "graph": {
                            "name": "Demo Graph",
                            "description": "A simple demonstration Graph.",
                            "nodes": [
                                {
                                    "name": "1",
                                    "relations": [],
                                    "constants": [
                                        "c"
                                    ]
                                },
                                {
                                    "name": "2",
                                    "relations": [],
                                    "constants": [
                                        "d"
                                    ]
                                }
                            ],
                            "edges": [
                                {
                                    "source": "1",
                                    "target": "2",
                                    "relations": [
                                        "B"
                                    ],
                                    "functions": []
                                },
                                {
                                    "source": "2",
                                    "target": "1",
                                    "relations": [
                                        "B"
                                    ],
                                    "functions": []
                                }
                            ]
                        }
                    }
        """.trimIndent()
      )
    }.run {
      response.status() shouldBe HttpStatusCode.OK
      response.content shouldNotBe null
    }
  }
}
