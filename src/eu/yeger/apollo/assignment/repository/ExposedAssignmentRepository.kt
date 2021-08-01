package eu.yeger.apollo.assignment.repository

import eu.yeger.apollo.assignment.model.persistence.PersistentAssignment
import eu.yeger.apollo.shared.repository.EntityTable
import eu.yeger.apollo.shared.repository.ExposedRepository
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

public class ExposedAssignmentRepository : AssignmentRepository, ExposedRepository<PersistentAssignment>(Assignments) {

  override fun ResultRow.toEntity(): PersistentAssignment {
    return PersistentAssignment(
      id = get(Assignments.id),
      title = get(Assignments.title),
      formula = get(Assignments.formula),
      description = get(Assignments.description).takeIf { it.isNotBlank() },
    )
  }

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
}

public object Assignments : EntityTable<PersistentAssignment>() {
  public val title: Column<String> = varchar("title", ASSIGNMENT_TITLE_MAX_LENGTH)
  public val formula: Column<String> = varchar("formula", ASSIGNMENT_FORMULA_MAX_LENGTH)
  public val description: Column<String> = varchar("description", ASSIGNMENT_DESCRIPTION_MAX_LENGTH)
}

public const val ASSIGNMENT_TITLE_MAX_LENGTH: Int = 30
public const val ASSIGNMENT_FORMULA_MAX_LENGTH: Int = 1000
public const val ASSIGNMENT_DESCRIPTION_MAX_LENGTH: Int = 1000
