package eu.yeger.apollo.model_checker

import eu.yeger.apollo.model_checker.model.api.ModelCheckerRequest
import eu.yeger.apollo.model_checker.service.ModelCheckerService
import eu.yeger.apollo.utils.post
import io.ktor.routing.*
import org.koin.ktor.ext.inject

/**
 * Appends all routes for ModelChecking to the given [Route].
 *
 * @receiver The base [Route].
 *
 * @author Jan Müller
 */
public fun Route.modelCheckerRoutes() {
  val modelCheckerService: ModelCheckerService by inject()

  post("model-checker") { request: ModelCheckerRequest ->
    modelCheckerService.checkModel(request)
  }
}
