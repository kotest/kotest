@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions

actual suspend fun invokeTestEngineLauncher(launcher: TestEngineLauncher) {

   val result = launcher
      .withTeamCityListener() // TCSM is always included to hook into the native test task reporting
      .withConsoleListener().launch()

   if (result.testFailures) {
      error("Tests failed")
   }

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
