package eu.yeger.apollo.utils

import eu.yeger.apollo.Arguments
import java.io.File
import kotlin.reflect.KProperty

private const val secretsDir = "/run/secrets"

public fun docker(secret: String, fallback: () -> String? = { null }): DockerDelegate {
  return DockerDelegate(secret, fallback)
}

public class DockerDelegate(private val secret: String, private val fallback: () -> String?) {

  public operator fun getValue(arguments: Arguments, property: KProperty<*>): String? {
    return try {
      val file = File("$secretsDir/$secret")
      file.readText().takeUnless { it.isBlank() }
    } catch (error: Throwable) {
      null
    } ?: fallback()
  }
}
