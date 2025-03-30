package com.sksamuel.kotest.timeout

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.interceptors.ProjectTimeoutException
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class ProjectTimeoutTest : FunSpec({

   test("a project times out when the sum duration of its tests exceeds the specified project timeout") {

      val c = object : AbstractProjectConfig() {
         override val projectTimeout = 10.milliseconds
      }

      // project timeout is set to 10
      // each test takes 5, but 3 tests, so we should easily hit project limit
      val result = TestEngineLauncher(NoopTestEngineListener)
         .withClasses(ProjectTimeoutSampleSpec::class)
         .withProjectConfig(c)
         .launch()

      result.errors.forOne { it.shouldBeInstanceOf<ProjectTimeoutException>() }
   }
})

private class ProjectTimeoutSampleSpec : FunSpec({

   test("1: a test under the test level timeout") {
      delay(5)
   }

   test("2: a test under the test level timeout") {
      delay(5)
   }

   test("3: a test under the test level timeout") {
      delay(5)
   }
})
