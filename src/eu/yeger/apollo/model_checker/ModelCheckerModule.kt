package eu.yeger.apollo.model_checker

import eu.yeger.apollo.model_checker.service.DefaultModelCheckerService
import eu.yeger.apollo.model_checker.service.ModelCheckerService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin-module containing the model-checker services of the backend.
 *
 * @author Jan Müller
 */
public val modelCheckerModule: Module = module {
  single<ModelCheckerService> {
    DefaultModelCheckerService()
  }
}
