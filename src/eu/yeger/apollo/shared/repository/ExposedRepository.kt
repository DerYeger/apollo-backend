package eu.yeger.apollo.shared.repository

import eu.yeger.apollo.shared.model.persistence.Entity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

public class ExposedEntityRetriever<T : Entity>(private val table: EntityTable<T>) : EntityRetriever<T> {
  override suspend fun getAll(): List<T> {
    return dbQuery {
      table.selectAll().map(table::rowToEntity)
    }
  }

  override suspend fun getById(id: String): T? {
    return dbQuery {
      table
        .select { table.id eq id }
        .map(table::rowToEntity)
        .singleOrNull()
    }
  }

  override suspend fun isEmpty(): Boolean {
    return dbQuery {
      table.selectAll().count() <= 0
    }
  }
}

public class ExposedEntityDeleter<T : Entity>(private val table: EntityTable<T>) : EntityDeleter<T> {
  override suspend fun deleteById(id: String): Boolean {
    return dbQuery {
      table.deleteWhere {
        table.id eq id
      }
    } > 1
  }
}

public class ExposedRepositoryBase<T : Entity>(
  table: EntityTable<T>
) : EntityRetriever<T> by ExposedEntityRetriever(table), EntityDeleter<T> by ExposedEntityDeleter(table)

public suspend fun <T> dbQuery(block: () -> T): T =
  withContext(Dispatchers.IO) {
    transaction { block() }
  }
