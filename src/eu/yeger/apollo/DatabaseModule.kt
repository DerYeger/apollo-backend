package eu.yeger.apollo

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import eu.yeger.apollo.Arguments.url
import io.ktor.application.*
import mu.KotlinLogging
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

private val logger = KotlinLogging.logger { }

public fun Application.databaseModule() {
  logger.debug { "Establishing database connection" }
  if (!Arguments.useDatabase) {
    logger.info { "Database configuration missing or incomplete. Aborting" }
    return
  }

  val dbUrl = Arguments.databaseUrl
  val dbUser = Arguments.databaseUser!!
  val dbPassword = Arguments.databasePassword!!

  val hikariConfig = HikariConfig().apply {
    driverClassName = "org.postgresql.Driver"
    jdbcUrl = dbUrl
    username = dbUser
    password = dbPassword
    maximumPoolSize = 3
    isAutoCommit = false
    transactionIsolation = "TRANSACTION_REPEATABLE_READ"
    validate()
  }
  val hikari = HikariDataSource(hikariConfig)

  logger.debug { "Connecting to $dbUrl" }
  Database.connect(hikari)
  logger.info { "Connected to $url" }

  logger.debug { "Beginning Flyway migration" }
  Flyway.configure()
    .validateMigrationNaming(true)
    .dataSource(dbUrl, dbUser, dbPassword)
    .locations("/db/migration")
    .baselineOnMigrate(true)
    .load()
    .migrate()
  logger.info { "Flyway migration completed" }
  logger.info { "Installation complete" }
}
