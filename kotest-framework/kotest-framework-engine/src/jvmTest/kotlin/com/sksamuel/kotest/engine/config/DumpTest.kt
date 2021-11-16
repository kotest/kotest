package com.sksamuel.kotest.engine.config

import io.kotest.core.config.Configuration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.config.createConfigSummary
import io.kotest.matchers.string.shouldInclude
import kotlin.time.seconds

@Isolate
class DumpTest : FunSpec({

   test("dump should include test timeouts") {
      Configuration().apply {
         timeout = 12
         invocationTimeout = 34234
         projectTimeout = 44444.seconds
      }.createConfigSummary().apply {
         this.shouldInclude("Default test timeout: 12ms")
         this.shouldInclude("Default test invocation timeout: 34234ms")
         this.shouldInclude("Overall project timeout: 12h 20m 44sms")
      }
   }

   test("dump should include affinity") {
      Configuration().apply {
         timeout = 12
         invocationTimeout = 34234
      }.createConfigSummary().apply {
         this.shouldInclude("Dispatcher affinity: true")
      }
   }

   test("dump should include test order") {
      Configuration().apply {
         testCaseOrder = TestCaseOrder.Random
      }.createConfigSummary().apply {
         this.shouldInclude("Default test execution order: Random")
      }
   }

   test("dump should include Spec execution order") {
      Configuration().apply {
         specExecutionOrder = SpecExecutionOrder.Annotated
      }.createConfigSummary().apply {
         this.shouldInclude("Spec execution order: Annotated")
      }
   }

   test("dump should include Duplicate test name mode") {
      Configuration().apply {
         duplicateTestNameMode = DuplicateTestNameMode.Silent
      }.createConfigSummary().apply {
         this.shouldInclude("Duplicate test name mode: Silent")
      }
   }

   test("dump should include default isolation mode") {
      Configuration().apply {
         isolationMode = IsolationMode.InstancePerLeaf
      }.createConfigSummary().apply {
         this.shouldInclude("Default isolation mode: InstancePerLeaf")
      }
   }

   test("dump should include failOnEmptyTestSuite") {
      Configuration().apply {
         failOnEmptyTestSuite = true
      }.createConfigSummary().apply {
         this.shouldInclude("Fail on empty test suite: true")
      }
   }

   test("dump should include coroutineDebugProbes") {
      Configuration().apply {
         coroutineDebugProbes = true
      }.createConfigSummary().apply {
         this.shouldInclude("Coroutine debug probe: true")
      }
   }

   test("dump should include failOnIgnoredTests") {
      Configuration().apply {
         failOnIgnoredTests = true
      }.createConfigSummary().apply {
         this.shouldInclude("Fail on ignored tests: true")
      }
   }

   test("dump should include globalAssertSoftly") {
      Configuration().apply {
         globalAssertSoftly = true
      }.createConfigSummary().apply {
         this.shouldInclude("Global soft assertions: true")
      }
   }
})
