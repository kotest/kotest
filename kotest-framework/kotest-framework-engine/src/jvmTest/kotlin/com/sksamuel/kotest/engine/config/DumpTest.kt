package com.sksamuel.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.config.createConfigSummary
import io.kotest.engine.config.toConfiguration
import io.kotest.matchers.string.shouldInclude
import kotlin.time.Duration.Companion.seconds

@Isolate
class DumpTest : FunSpec({

   test("dump should include test timeouts") {
      MutableConfiguration().apply {
         timeout = 12
         invocationTimeout = 34234
         projectTimeout = 44444.seconds
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Default test timeout: 12ms")
         this.shouldInclude("Default test invocation timeout: 34234ms")
         this.shouldInclude("Overall project timeout: 12h 20m 44sms")
      }
   }

   test("dump should include affinity") {
      MutableConfiguration().apply {
         timeout = 12
         invocationTimeout = 34234
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Dispatcher affinity: true")
      }
   }

   test("dump should include test order") {
      MutableConfiguration().apply {
         testCaseOrder = TestCaseOrder.Random
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Default test execution order: Random")
      }
   }

   test("dump should include Spec execution order") {
      MutableConfiguration().apply {
         specExecutionOrder = SpecExecutionOrder.Annotated
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Spec execution order: Annotated")
      }
   }

   test("dump should include Duplicate test name mode") {
      MutableConfiguration().apply {
         duplicateTestNameMode = DuplicateTestNameMode.Silent
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Duplicate test name mode: Silent")
      }
   }

   test("dump should include default isolation mode") {
      MutableConfiguration().apply {
         isolationMode = IsolationMode.InstancePerLeaf
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Default isolation mode: InstancePerLeaf")
      }
   }

   test("dump should include failOnEmptyTestSuite") {
      MutableConfiguration().apply {
         failOnEmptyTestSuite = true
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Fail on empty test suite: true")
      }
   }

   test("dump should include coroutineDebugProbes") {
      MutableConfiguration().apply {
         coroutineDebugProbes = true
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Coroutine debug probe: true")
      }
   }

   test("dump should include failOnIgnoredTests") {
      MutableConfiguration().apply {
         failOnIgnoredTests = true
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Fail on ignored tests: true")
      }
   }

   test("dump should include globalAssertSoftly") {
      MutableConfiguration().apply {
         globalAssertSoftly = true
      }.toConfiguration().createConfigSummary().apply {
         this.shouldInclude("Global soft assertions: true")
      }
   }
})
