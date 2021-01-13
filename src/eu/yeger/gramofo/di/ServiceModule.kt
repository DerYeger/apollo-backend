package eu.yeger.gramofo.di

import eu.yeger.gramofo.service.DefaultModelCheckerService
import eu.yeger.gramofo.service.ModelCheckerService
import org.koin.dsl.module

/**
 * Koin-module containing all services of the backend.
 *
 * @author Jan Müller
 */
val serviceModule = module {
    single<ModelCheckerService> {
        DefaultModelCheckerService()
    }
}
