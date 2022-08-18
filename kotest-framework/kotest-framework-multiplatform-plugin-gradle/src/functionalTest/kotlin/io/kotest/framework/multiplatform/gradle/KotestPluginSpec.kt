package io.kotest.framework.multiplatform.gradle

import io.kotest.assertions.asClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.framework.multiplatform.gradle.util.GradleKtsProjectTest.Companion.gradleKtsProjectTest
import io.kotest.framework.multiplatform.gradle.util.GradleProjectTest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.TaskOutcome

class KotestPluginSpec : FunSpec({

   val mavenInternalDir: String = System.getProperty("mavenInternalDir")

   context("verify Kotest plugin can be applied") {

      listOf(
         "macosArm64Test",
         "macosX64Test",
         "mingwX64Test",
         "linuxX64Test",
      ).forEach { nativeTargetTest ->

         listOf(
            true,
            false,
         ).forEach { useNewNativeMemoryModel ->

            listOf(
               "1.6.21",
               "1.7.0",
               "1.7.10",
            ).forEach { kotlinVersion ->

               context("kotlin $kotlinVersion, useNewNativeMemoryModel=$useNewNativeMemoryModel") {
                  val kotestVersion = KOTEST_COMPILER_PLUGIN_VERSION
//            val useNewNativeMemoryModel = true

                  val gradleTest = gradleKtsProjectTest {
                     buildGradleKts = """
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
   id("org.jetbrains.kotlin.multiplatform") version "$kotlinVersion"
   id("io.kotest.multiplatform")
}

repositories {
  maven(file("$mavenInternalDir"))
  mavenCentral()
}

kotlin {
   jvm()

   js(IR) {
      // FIXME: re-enable this once the issue described in https://github.com/kotest/kotest/pull/3107#issue-1301849119 is fixed
      // browser()
      nodejs()
   }

   linuxX64()
   macosX64()
   macosArm64()
   mingwX64()

   sourceSets {
      val commonTest by getting {
         dependencies {
            implementation("io.kotest:kotest-assertions-core:$kotestVersion")
            implementation("io.kotest:kotest-framework-api:$kotestVersion")
            implementation("io.kotest:kotest-framework-engine:$kotestVersion")
         }
      }

      val jvmTest by getting {
         dependencies {
            implementation("io.kotest:kotest-runner-junit5:$kotestVersion")
         }
      }
   }
}

tasks.named<Test>("jvmTest") {
   useJUnitPlatform()
}

tasks.withType<AbstractTestTask>().configureEach {
   testLogging {
      showExceptions = true
      showStandardStreams = true
      events = setOf(TestLogEvent.FAILED, TestLogEvent.SKIPPED, TestLogEvent.STANDARD_ERROR, TestLogEvent.STANDARD_OUT)
      exceptionFormat = TestExceptionFormat.FULL
   }
}

if ($useNewNativeMemoryModel) {
   kotlin.targets.withType(KotlinNativeTarget::class.java) {
      binaries.all {
         binaryOptions["memoryModel"] = "experimental"
      }
   }
}
""".trimIndent()

                     createFile(
                        "src/commonMain/kotlin/TestStrings.kt", /* language=Kotlin */ """
object TestStrings {
   val helloWorld = "Hello world!"
}
""".trimIndent()
                     )
                     createFile(
                        "src/commonTest/kotlin/TestSpec.kt",/* language=Kotlin */ """
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe

class TestSpec : ShouldSpec({
   should("be able to do arithmetic") {
      1 + 1 shouldBe 2
   }

   should("be able to use main source set") {
      TestStrings.helloWorld shouldBe "Hello world!"
   }
})
""".trimIndent()
                     )
                  }
                  `verify Gradle can configure the project`(gradleTest)
                  `verify Kotest plugin warnings`(gradleTest)
                  `run test task`(gradleTest, ":jvmTest")
               }
            }
         }
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

      private suspend fun FunSpecContainerScope.`run test task`(
         gradleProjectTest: GradleProjectTest,
         testTask: String,
      ) {
         context("verify task $testTask can be run") {
            val result = gradleProjectTest.runner
               .withArguments(testTask, "--info", "--stacktrace")
               .withPluginClasspath()
               .build()

            result.output.asClue {
               test("expect task :$testTask is successful") {
                  result.output shouldContain "BUILD SUCCESSFUL"
                  result.task(testTask)?.outcome shouldBe TaskOutcome.SUCCESS
               }
            }
         }
      }
   }
}
