package eu.yeger.apollo

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.util.argument
import eu.yeger.apollo.utils.docker
import mu.KotlinLogging

private val logger = KotlinLogging.logger { }

public object Arguments : Arkenv() {
  public val url: String by argument {
    defaultValue = { "localhost" }
    validate("URL may not be empty or blank.", String::isNotBlank)
  }

  private val defaultUsernameDocker: String? by docker("default_username")
  private val defaultUsernameArgument: String by argument("DEFAULT_USERNAME") {
    defaultValue = { "apollo-admin" }
    validate("Default username may not be empty or blank.", String::isNotBlank)
  }
  public val defaultUsername: String
    get() = defaultUsernameDocker ?: defaultUsernameArgument

  private val defaultPasswordDocker: String? by docker("default_password")
  private val defaultPasswordArgument: String by argument("DEFAULT_PASSWORD") {
    defaultValue = { "apollo-admin" }
    validate("Default password may not be empty or blank.", String::isNotBlank)
  }
  public val defaultPassword: String
    get() = defaultPasswordDocker ?: defaultPasswordArgument

  private val jwtSecretDocker: String? by docker("jwt_secret")
  private val jwtSecretArgument: String? by argument("JWT_SECRET")
  public val jwtSecret: String?
    get() = jwtSecretDocker ?: jwtSecretArgument

  public val databaseHost: String? by argument()
  public val databasePort: String? by argument()
  public val databaseName: String? by argument()

  private val databaseUserDocker: String? by docker("database_user")
  private val databaseUserArgument: String? by argument("DATABASE_USER")
  public val databaseUser: String?
    get() = databaseUserDocker ?: databaseUserArgument

  private val databasePasswordDocker: String? by docker("database_password")
  private val databasePasswordArgument: String? by argument("DATABASE_USER")
  public val databasePassword: String?
    get() = databasePasswordDocker ?: databasePasswordArgument

  public val useDatabase: Boolean
    get() = databaseHost != null && databasePort != null && databaseName != null && databaseUser != null && databasePassword != null

  public val databaseUrl: String
    get() = "jdbc:postgresql://$databaseHost:$databasePort/$databaseName"
}
