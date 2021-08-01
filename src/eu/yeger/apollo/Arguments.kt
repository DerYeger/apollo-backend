package eu.yeger.apollo

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.util.argument

public object Arguments : Arkenv() {
  public val url: String by argument {
    defaultValue = { "localhost" }
    validate("URL may not be empty or blank.", String::isNotBlank)
  }

  public val defaultUsername: String by argument {
    defaultValue = { "apollo-admin" }
    validate("Default username may not be empty or blank.", String::isNotBlank)
  }

  public val defaultPassword: String by argument {
    defaultValue = { "apollo-admin" }
    validate("Default password may not be empty or blank.", String::isNotBlank)
  }

  public val databaseHost: String? by argument()
  public val databasePort: String? by argument()
  public val databaseName: String? by argument()

  public val databaseUser: String? by argument()
  public val databasePassword: String? by argument()

  public val useDatabase: Boolean
    get() = databaseHost != null && databasePort != null && databaseName != null && databaseUser != null && databasePassword != null

  public val databaseUrl: String
    get() = "jdbc:postgresql://$databaseHost:$databasePort/$databaseName"
}
