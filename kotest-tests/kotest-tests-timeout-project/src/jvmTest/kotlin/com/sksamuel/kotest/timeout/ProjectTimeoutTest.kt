package com.sksamuel.kotest.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.interceptors.ProjectTimeoutException
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
class ProjectTimeoutTest : FunSpec({

   test("a project times out when the sum duration of its tests exceeds the specified project timeout") {

      val c = ProjectConfiguration()
      c.projectTimeout = 10.milliseconds

      // project timeout is set to 30
      // each test takes 25, but 3 tests, so we should easily hit project limit
      val result = TestEngineLauncher(NoopTestEngineListener)
         .withClasses(ProjectTimeoutSampleSpec::class)
         .withConfiguration(c)
         .launch()

      result.errors.forOne { it.shouldBeInstanceOf<ProjectTimeoutException>() }
   }
})

private class ProjectTimeoutSampleSpec : FunSpec({

   test("1: a test under the test level timeout") {
      delay(25)
   }

   test("2: a test under the test level timeout") {
      delay(25)
   }

   test("3: a test under the test level timeout") {
      delay(25)
   }
})
