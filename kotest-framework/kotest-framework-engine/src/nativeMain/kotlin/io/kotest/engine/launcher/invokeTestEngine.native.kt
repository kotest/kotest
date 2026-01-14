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
      // we don't need this from inside intellij as the test output will be present in the tree view anyway,
      // but we don't have a way of detecting intellij from kotlin native so aren't able to detect when to skip
      .withConsoleListener()
      // TCSM is always included to hook into the native test task reporting
      // also, the Gradle test task will capture stdout when it receives a TCSM test-started event until it receives
      // a test-finished event, so this TCSM listener must come after the console listener, otherwise, some console
      // output will be swallowed
      .withTeamCityListener()
      .async()

   if (result.errors.isNotEmpty()) {
      throw MultipleExceptions(result.errors)
   }
}
