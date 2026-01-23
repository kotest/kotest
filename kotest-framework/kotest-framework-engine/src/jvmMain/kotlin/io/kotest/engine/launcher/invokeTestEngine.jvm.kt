@file:Suppress("unused")

package io.kotest.engine.launcher

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions

actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {

   val result = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)
      .execute()

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }

}
