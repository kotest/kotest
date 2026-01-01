@file:Suppress("unused")

package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions
import kotlin.system.exitProcess

actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {

   val result = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)
      .async()

   if (result.testFailures) {
      // the kotest task test will pick up return code as 1 as failed errors
      exitProcess(1)
   }

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }

}
