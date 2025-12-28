@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions
import kotlin.system.exitProcess

actual suspend fun invokeTestEngineLauncher(launcher: TestEngineLauncher) {

   val result = launcher.async()

   if (result.testFailures) {
      // the kotest task test will pick up return code as 1 as failed errors
      exitProcess(1)
   }

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }

}
