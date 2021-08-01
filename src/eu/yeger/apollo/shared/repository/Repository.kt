package eu.yeger.apollo.shared.repository

import eu.yeger.apollo.shared.model.persistence.Entity

public interface EntityRetriever<T : Entity> {
  public suspend fun getAll(): List<T>

  public suspend fun getById(id: String): T?

  public suspend fun isEmpty(): Boolean
}

public interface EntitySaver<T : Entity> {
  public suspend fun save(entity: T)
}

public interface EntityUpdater<T : Entity> {
  public suspend fun update(entity: T)
}

public interface EntityDeleter<T : Entity> {
  public suspend fun deleteById(id: String): Boolean
}

public interface Repository<T : Entity> : EntityRetriever<T>, EntitySaver<T>, EntityUpdater<T>, EntityDeleter<T>
