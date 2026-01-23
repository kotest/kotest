package io.kotest.engine.launcher

import com.github.ajalt.mordant.platform.MultiplatformSystem.exitProcess
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecRef
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.MultipleExceptions
import io.kotest.engine.js.JsTestFrameworkTestEngineListener
import io.kotest.engine.js.isJavaScriptTestFrameworkAvailable
import io.kotest.engine.js.kotlinJsTestFramework
import io.kotest.engine.listener.TeamCityTestEngineListener
import kotlinx.coroutines.DelicateCoroutinesApi

@OptIn(DelicateCoroutinesApi::class)
@Suppress("UNCHECKED_CAST", "unused")
actual suspend fun invokeTestEngine(specs: List<SpecRef>, config: AbstractProjectConfig?) {

   val listener = if (isJavaScriptTestFrameworkAvailable())
      JsTestFrameworkTestEngineListener(kotlinJsTestFramework)
   else
      TeamCityTestEngineListener()

   val launcher = TestEngineLauncher()
      .withSpecRefs(specs)
      .withProjectConfig(config)
      .withListener(listener)

   val result = launcher.execute()

   when (result.errors.size) {
      0 -> Unit
      1 -> handleEngineError(result.errors.first())
      else -> handleEngineError(MultipleExceptions(result.errors))
   }
}

private fun handleEngineError(error: Throwable) {
   // throwing an exception won't fail the build in intellij (it will show the stack trace),
   // so we'll handle exiting ourselves to ensure the build fails
   println(error)
   exitProcess(1)
}
