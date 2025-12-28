@file:Suppress("unused")

package io.kotest.engine.errors

import io.kotest.engine.EngineResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions
import kotlinx.coroutines.await
import kotlin.js.Promise

@Suppress("UNCHECKED_CAST")
actual suspend fun invokeTestEngineLauncher(launcher: TestEngineLauncher) {
   val promise = launcher
      .withConsoleListener()
      .promise() as Promise<EngineResult>
   val result = promise.await()

   if (result.testFailures) {
      error("Tests failed")
   }
   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
