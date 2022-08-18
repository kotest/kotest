package io.kotest.framework.multiplatform.gradle

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.framework.multiplatform.gradle.util.GradleGroovyProjectTest.Companion.gradleGroovyProjectTest
import io.kotest.framework.multiplatform.gradle.util.GradleKtsProjectTest.Companion.gradleKtsProjectTest
import io.kotest.framework.multiplatform.gradle.util.GradleProjectTest
import io.kotest.inspectors.forOne
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.TaskOutcome

class KotestPluginExtensionSpec : FunSpec({

   context("verify Kotest compiler version can be set manually") {

      context("kts") {
         val gradleTest = gradleKtsProjectTest {
            buildGradleKts = """
plugins {
  base
  id("io.kotest.multiplatform")
  kotlin("multiplatform")
}

kotest {
  kotestCompilerPluginVersion.set("1.2.3")
}

kotlin {
  jvm()

  sourceSets {
    val commonTest by getting {
      dependencies {
        // the version here is not considered
        implementation("io.kotest:kotest-framework-engine:4.5.6")
      }
    }
  }
}

val printKotestCompilerPluginVersion by tasks.registering {
  val kotestCompilerPluginVersion = kotest.kotestCompilerPluginVersion
  doLast {
    logger.lifecycle("printKotestCompilerPluginVersion: " + kotestCompilerPluginVersion.orNull)
  }
}
""".trimIndent()
         }
         `verify Gradle can configure the project`(gradleTest)
         `verify Kotest compiler version is set`(gradleTest, "1.2.3")
         `verify Kotest plugin warnings`(gradleTest)
      }

      context("groovy") {
         val gradleTest = gradleGroovyProjectTest {
            buildGradle = """
plugins {
  id "io.kotest.multiplatform"
  id "org.jetbrains.kotlin.multiplatform"
}

kotest {
  kotestCompilerPluginVersion = "1.2.3"
}

kotlin {
  jvm()
}

tasks.register('printKotestCompilerPluginVersion') {
  Provider<String> kecVersionProvider = kotest.kotestCompilerPluginVersion
  doLast {
    String kecVersion = kecVersionProvider.getOrNull()
    logger.lifecycle("printKotestCompilerPluginVersion: " + kecVersion)
  }
}
""".trimIndent()
         }

         `verify Gradle can configure the project`(gradleTest)
         `verify Kotest compiler version is set`(gradleTest, "1.2.3")
         `verify Kotest plugin warnings`(gradleTest)
      }
   }

   context("verify Kotest compiler version defaults to constant") {
      context("kts") {
         val gradleTest = gradleKtsProjectTest {
            buildGradleKts = """
plugins {
  base
  id("io.kotest.multiplatform")
  kotlin("multiplatform")
}

kotest {
  // do not manually set kotestCompilerPluginVersion
}

kotlin {
  jvm()

  sourceSets {
    val commonTest by getting {
      dependencies {
        // the version here is not considered
        implementation("io.kotest:kotest-framework-engine:4.5.6")
      }
    }
  }
}

val printKotestCompilerPluginVersion by tasks.registering {
  val kotestCompilerPluginVersion = kotest.kotestCompilerPluginVersion
  doLast {
    logger.lifecycle("printKotestCompilerPluginVersion: " + kotestCompilerPluginVersion.orNull)
  }
}
""".trimIndent()
         }

         `verify Gradle can configure the project`(gradleTest)
         `verify Kotest compiler version is set`(gradleTest, KOTEST_COMPILER_PLUGIN_VERSION)
         `verify Kotest plugin warnings`(gradleTest)
      }
   }

}) {
   companion object {
      private suspend fun FunSpecContainerScope.`verify Gradle can configure the project`(
         gradleProjectTest: GradleProjectTest
      ) {
         context("verify Gradle can configure the project") {
            val result = gradleProjectTest.runner
               .withArguments(":tasks", "--info", "--stacktrace")
               .withPluginClasspath()
               .build()

            result.output.asClue {
               test("expect tasks can be listed") {
                  result.output shouldContain "BUILD SUCCESSFUL"
                  result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
               }
            }
         }
      }

      private suspend fun FunSpecContainerScope.`verify Kotest plugin warnings`(
         gradleProjectTest: GradleProjectTest
      ) {
         context("verify Kotest plugin warnings") {
            val result = gradleProjectTest.runner
               .withArguments(":tasks", "--info", "--stacktrace")
               .withPluginClasspath()
               .build()

            result.output.asClue {
               test("expect no Kotest plugin warnings") {
                  result.output shouldNotContain "Warning: Kotest plugin has been added to root project 'kotest-plugin-test'"
                  result.output shouldNotContain "but could not determine Kotest engine version"
                  result.output shouldNotContain "Kotest will not be enabled"
               }
            }
         }
      }

      private suspend fun FunSpecContainerScope.`verify Kotest compiler version is set`(
         gradleProjectTest: GradleProjectTest,
         expectedVersion: String,
      ) {
         context("verify Kotest compiler version is set") {
            val result = gradleProjectTest.runner
               .withArguments(":printKotestCompilerPluginVersion", "--info", "--stacktrace")
               .withPluginClasspath()
               .build()

            result.output.asClue {
               test("expect task :printKotestCompilerPluginVersion is successful") {
                  result.output shouldContain "BUILD SUCCESSFUL"
                  result.task(":printKotestCompilerPluginVersion")?.outcome shouldBe TaskOutcome.SUCCESS
               }
            }
            val testLines = result.output.lines().filter { it.startsWith("printKotestCompilerPluginVersion") }
            testLines.asClue {
               test("expect :printKotestCompilerPluginVersion prints $expectedVersion") {
                  testLines.forOne { line ->
                     line shouldContain "printKotestCompilerPluginVersion: $expectedVersion"
                  }
               }
            }
         }
      }
   }
}
