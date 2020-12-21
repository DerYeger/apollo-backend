package eu.yeger.gramofo.di

import eu.yeger.gramofo.service.DefaultModelCheckerService
import eu.yeger.gramofo.service.ModelCheckerService
import org.koin.dsl.module

val serviceModule = module {
    single<ModelCheckerService> {
        DefaultModelCheckerService()
    }
}
