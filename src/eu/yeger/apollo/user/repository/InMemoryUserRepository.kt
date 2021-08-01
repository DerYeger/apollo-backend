package eu.yeger.apollo.user.repository

import eu.yeger.apollo.shared.repository.InMemoryRepository
import eu.yeger.apollo.shared.repository.Repository
import eu.yeger.apollo.user.model.persistence.PersistentUser
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

public class InMemoryUserRepository(
  private val userMap: ConcurrentMap<String, PersistentUser> = ConcurrentHashMap()
) : UserRepository, Repository<PersistentUser> by InMemoryRepository(userMap) {

  override suspend fun getByName(name: String): PersistentUser? {
    return userMap.values.find { it.name == name }
  }
}
