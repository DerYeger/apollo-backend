pluginManagement {
  val dokkaVersion: String by settings
  val kotlinVersion: String by settings
  val ktlintVersion: String by settings
  val shadowVersion: String by settings

  plugins {
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") 1.6.21 kotlinVersion
    id("org.jetbrains.dokka") version dokkaVersion
    id("org.jlleitschuh.gradle.ktlint") version ktlintVersion
    id("com.github.johnrengelman.shadow") version shadowVersion
  }
}

rootProject.name = "apollo-backend"
