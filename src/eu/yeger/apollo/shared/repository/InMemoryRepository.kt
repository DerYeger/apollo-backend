package eu.yeger.apollo.shared.repository

import eu.yeger.apollo.shared.model.persistence.Entity
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

public class InMemoryRepository<T : Entity>(
  private val entityMap: ConcurrentMap<String, T> = ConcurrentHashMap()
) : Repository<T> {

  override suspend fun getAll(): List<T> {
    return entityMap.values.toList()
  }

  override suspend fun getById(id: String): T? {
    return entityMap[id]
  }

  override suspend fun isEmpty(): Boolean {
    return entityMap.isEmpty()
  }

  override suspend fun save(entity: T) {
    entityMap[entity.id] = entity
  }

  override suspend fun deleteById(id: String): Boolean {
    return entityMap.remove(id) != null
  }
}
