package com.sksamuel.kotest.engine.launcher

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.launcher.LauncherArgs
import io.kotest.engine.launcher.setupLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.booleans.shouldBeTrue

@Isolate
class SetupLauncherTest : FunSpec() {
   init {
      test("setupLauncher should return an error for unknown class") {
         setupLauncher(LauncherArgs(null, null, "unknown.class", null, null, null, false), NoopTestEngineListener)
            .isFailure.shouldBeTrue()
      }
   }
}
