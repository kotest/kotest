package com.sksamuel.kotest.engine.launcher

import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.engine.launcher.LauncherArgs
import io.kotest.engine.launcher.setupLauncher
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.mockk.InternalPlatformDsl.toArray
import kotlin.reflect.KClass

@Isolate
class SetupLauncherTest : FunSpec() {
   init {
      test("setupLauncher should return an error for unknown class") {
         setupLauncher(LauncherArgs(null, null, "unknown.class", null, null, null), NoopTestEngineListener)
            .isFailure.shouldBeTrue()
      }
   }
}
