package eu.yeger.apollo.shared.repository

import eu.yeger.apollo.shared.model.persistence.Entity
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

public abstract class EntityTable<T : Entity> : Table() {
  public val id: Column<String> = varchar("id", 36).autoIncrement()
  public override val primaryKey: Table.PrimaryKey = PrimaryKey(id)
}
