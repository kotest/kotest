package io.kotest.plugin.intellij.run

import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.dependencies.ModuleDependencies
import io.kotest.plugin.intellij.gradle.GradleUtils

enum class RunnerMode {

   // the standard Gradle test tasks used by JUnit and Kotlin multiplatform.
   // Starting with Kotest 6.1, this is the preferred method of running tests.
   GRADLE_TEST_TASK,

   // the Kotest Gradle test task introduced in 6.0
   @Deprecated("Starting with Kotest 6.1 the preferred method is to run via gradle test task")
   GRADLE_KOTEST_TASK,

   // Run via invoking the engine through a main method. Only useful for Maven users currently.
   @Deprecated("Starting with Kotest 6.1 the preferred method is to run via gradle test task")
   LEGACY
}

@Suppress("DEPRECATION")
object RunnerModes {

   fun mode(module: Module?): RunnerMode? {
      if (module == null) return null
      if (!ModuleDependencies.hasKotestEngine(module)) return null

      // if we are on Kotest 6.1 or higher, and the test runner is Gradle, then we default to the Gradle test task
      // this doesn't require the Kotest Gradle plugin to be added for JVM.
      if (GradleUtils.isKotest61OrAbove(module) && GradleUtils.isGradleTestRunner(module)) return RunnerMode.GRADLE_TEST_TASK

      // if we have the Kotest Gradle plugin, then we use the Kotest task
      if (GradleUtils.hasKotestGradlePlugin(module)) return RunnerMode.GRADLE_KOTEST_TASK

      // otherwise we fall back to the legacy runners
      return RunnerMode.LEGACY
   }
}
