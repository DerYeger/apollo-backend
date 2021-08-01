package eu.yeger.apollo.user

import eu.yeger.apollo.Arguments
import eu.yeger.apollo.user.repository.ExposedUserRepository
import eu.yeger.apollo.user.repository.InMemoryUserRepository
import eu.yeger.apollo.user.repository.UserRepository
import eu.yeger.apollo.user.service.DefaultUserService
import eu.yeger.apollo.user.service.UserService
import mu.KotlinLogging
import org.koin.core.module.Module
import org.koin.dsl.module

private val kotlinLogger = KotlinLogging.logger { }

/**
 * Koin-module containing user services and repositories.
 *
 * @author Jan Müller
 */
public val userModule: Module = module {
  single<UserService> {
    DefaultUserService(get())
  }
  single<UserRepository> {
    if (Arguments.useDatabase) {
      ExposedUserRepository()
    } else {
      InMemoryUserRepository()
    }.also { kotlinLogger.debug { "Using ${it::class.java.simpleName}" } }
  }
}
