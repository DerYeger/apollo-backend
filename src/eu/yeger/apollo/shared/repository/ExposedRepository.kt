package eu.yeger.apollo.shared.repository

import eu.yeger.apollo.shared.model.persistence.Entity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

public abstract class ExposedRepository<T : Entity>(private val table: EntityTable<T>) : Repository<T> {

  public abstract fun ResultRow.toEntity(): T

  protected suspend fun <T> dbQuery(block: () -> T): T =
    withContext(Dispatchers.IO) {
      transaction { block() }
    }

  override suspend fun getAll(): List<T> {
    return dbQuery {
      table.selectAll().map { it.toEntity() }
    }
  }

  override suspend fun getById(id: String): T? {
    return dbQuery {
      table.select { table.id eq id }.mapNotNull { it.toEntity() }.singleOrNull()
    }
  }

  override suspend fun deleteById(id: String): Boolean {
    return dbQuery {
      table.deleteWhere {
        table.id eq id
      }
    } > 1
  }
}
