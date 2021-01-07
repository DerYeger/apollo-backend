package eu.yeger.gramofo.routing

import eu.yeger.gramofo.mainModule
import eu.yeger.gramofo.utils.shouldBe
import eu.yeger.gramofo.utils.shouldNotBe
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class ModelCheckerRouteTests {

    @Test
    fun `verify that the route is configured properly`() {
        withTestApplication({
            mainModule()
            routingModule()
        }) {
            runBlocking {
                (0..5).forEach { _ -> launch { makeCall("en", true) } }
                (0..5).forEach { _ -> launch { makeCall("de", false) } }
            }
        }
    }

    private fun TestApplicationEngine.makeCall(language: String, minimizeResult: Boolean) {
        handleRequest {
            method = HttpMethod.Post
            uri = "/modelchecker"
            addHeader("Content-Type", "application/json")
            setBody(
                """
                    {
                        "formula": "exists x. exists y. B(x,y)",
                        "language": "$language",
                        "minimizeResult": $minimizeResult,
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
            requestHandled shouldBe true
            response.status() shouldBe HttpStatusCode.OK
            response.content shouldNotBe null
        }
    }
}
