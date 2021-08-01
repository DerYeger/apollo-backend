package eu.yeger.apollo.assignment.repository

import eu.yeger.apollo.assignment.model.persistence.PersistentAssignment
import eu.yeger.apollo.shared.repository.*
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

public class ExposedAssignmentRepository :
  AssignmentRepository,
  EntitySaver<PersistentAssignment>,
  EntityUpdater<PersistentAssignment>,
  EntityRetriever<PersistentAssignment> by ExposedEntityRetriever(Assignments),
  EntityDeleter<PersistentAssignment> by ExposedEntityDeleter(Assignments) {

  override suspend fun save(entity: PersistentAssignment) {
    dbQuery {
      Assignments.insert {
        it[id] = entity.id
        it[title] = entity.title
        it[formula] = entity.formula
        it[description] = entity.description.orEmpty()
      }
    }
  }

  override suspend fun update(entity: PersistentAssignment) {
    dbQuery {
      Assignments.update {
        it[id] = entity.id
        it[title] = entity.title
        it[formula] = entity.formula
        it[description] = entity.description.orEmpty()
      }
    }
  }
}

public object Assignments : EntityTable<PersistentAssignment>() {
  public val title: Column<String> = varchar("title", ASSIGNMENT_TITLE_MAX_LENGTH)
  public val formula: Column<String> = varchar("formula", ASSIGNMENT_FORMULA_MAX_LENGTH)
  public val description: Column<String> = varchar("description", ASSIGNMENT_DESCRIPTION_MAX_LENGTH)

  public override fun rowToEntity(row: ResultRow): PersistentAssignment {
    return PersistentAssignment(
      id = row[id],
      title = row[title],
      formula = row[formula],
      description = row[description].takeIf { it.isNotBlank() },
    )
  }
}

public const val ASSIGNMENT_TITLE_MAX_LENGTH: Int = 30
public const val ASSIGNMENT_FORMULA_MAX_LENGTH: Int = 1000
public const val ASSIGNMENT_DESCRIPTION_MAX_LENGTH: Int = 1000
