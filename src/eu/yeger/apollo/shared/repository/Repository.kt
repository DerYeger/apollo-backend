package eu.yeger.apollo.shared.repository

import eu.yeger.apollo.shared.model.persistence.Entity

public interface Repository<T : Entity> {

  public suspend fun getAll(): List<T>

  public suspend fun getById(id: String): T?

  public suspend fun isEmpty(): Boolean

  public suspend fun save(entity: T)

  public suspend fun deleteById(id: String): Boolean
}
