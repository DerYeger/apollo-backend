package eu.yeger.apollo.user.repository

import eu.yeger.apollo.shared.repository.*
import eu.yeger.apollo.user.model.persistence.PersistentUser
import org.jetbrains.exposed.sql.*

public class ExposedUserRepository :
  UserRepository,
  EntitySaver<PersistentUser>,
  EntityUpdater<PersistentUser>,
  EntityRetriever<PersistentUser> by ExposedEntityRetriever(Users),
  EntityDeleter<PersistentUser> by ExposedEntityDeleter(Users) {

  override suspend fun getByName(name: String): PersistentUser? {
    return dbQuery {
      Users
        .select { Users.name eq name }
        .map(Users::rowToEntity)
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

  override suspend fun update(entity: PersistentUser) {
    dbQuery {
      Users.update {
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

  public override fun rowToEntity(row: ResultRow): PersistentUser {
    return PersistentUser(
      id = row[id],
      name = row[name],
      password = row[password].toString(Charsets.UTF_8),
    )
  }
}

public const val USER_NAME_MAX_LENGTH: Int = 30
