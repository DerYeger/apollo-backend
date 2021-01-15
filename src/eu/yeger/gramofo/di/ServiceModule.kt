package eu.yeger.gramofo.di

import eu.yeger.gramofo.service.DefaultModelCheckerService
import eu.yeger.gramofo.service.ModelCheckerService
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Koin-module containing all services of the backend.
 *
 * @author Jan Müller
 */
public val serviceModule: Module = module {
    single<ModelCheckerService> {
        DefaultModelCheckerService()
    }
}
