package eu.yeger.apollo.user.repository

import eu.yeger.apollo.shared.repository.EntityTable
import eu.yeger.apollo.shared.repository.ExposedRepository
import eu.yeger.apollo.user.model.persistence.PersistentUser
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

public class ExposedUserRepository : UserRepository, ExposedRepository<PersistentUser>(Users) {

  override fun ResultRow.toEntity(): PersistentUser {
    return PersistentUser(
      id = get(Users.id),
      name = get(Users.name),
      password = get(Users.password).toString(Charsets.UTF_8),
    )
  }

  override suspend fun getByName(name: String): PersistentUser? {
    return dbQuery {
      Users
        .select { Users.name eq name }
        .map { it.toEntity() }
        .singleOrNull()
    }
  }

  override suspend fun save(entity: PersistentUser) {
    dbQuery {
      Users.insert {
        it[id] = entity.id
        it[name] = entity.name
        it[password] = entity.password.toByteArray(Charsets.UTF_8)
      }
    }
  }
}

public object Users : EntityTable<PersistentUser>() {
  public val name: Column<String> = varchar("name", USER_NAME_MAX_LENGTH).uniqueIndex()
  public val password: Column<ByteArray> = binary("password")
}

public const val USER_NAME_MAX_LENGTH: Int = 30
