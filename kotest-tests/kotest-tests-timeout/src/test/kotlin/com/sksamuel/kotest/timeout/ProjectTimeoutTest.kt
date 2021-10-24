package com.sksamuel.kotest.timeout

import io.kotest.core.config.configuration
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.interceptors.ProjectTimeoutException
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.inspectors.forOne
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds

@Isolate
class ProjectTimeoutTest : FunSpec({

   var projectTimeout: Duration? = Duration.INFINITE
   var testTimeout: Long = Long.MAX_VALUE

   beforeSpec {
      projectTimeout = configuration.projectTimeout
      testTimeout = configuration.timeout
      configuration.projectTimeout = 10.milliseconds
      // need to reset the timeout per test since we're testing project timeouts
      configuration.timeout = Long.MAX_VALUE
   }

   afterSpec {
      configuration.projectTimeout = projectTimeout
      configuration.timeout = testTimeout
   }

   test("a project times out when the sum duration of its tests exceeds the specified project timeout") {
      // project timeout is set to 10
      // each test takes 10, but 3 tests, so we should hit the project limit
      val result = TestEngineLauncher(NoopTestEngineListener)
         .withClasses(ProjectTimeoutSampleSpec::class)
         .launch()
      result.errors.forOne { it.shouldBeInstanceOf<ProjectTimeoutException>() }
   }
})

private class ProjectTimeoutSampleSpec : FunSpec({

   test("1: a test under the test level timeout") {
      delay(10)
   }

   test("2: a test under the test level timeout") {
      delay(10)
   }

   test("3: a test under the test level timeout") {
      delay(10)
   }
})
