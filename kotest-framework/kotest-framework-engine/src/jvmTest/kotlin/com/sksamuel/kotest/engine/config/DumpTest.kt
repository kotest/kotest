package com.sksamuel.kotest.engine.config

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.config.createConfigSummary
import io.kotest.matchers.string.shouldInclude
import kotlin.time.Duration.Companion.seconds

@Isolate
class DumpTest : FunSpec({

   test("dump should include test timeouts") {
      ProjectConfiguration().apply {
         timeout = 12
         invocationTimeout = 34234
         projectTimeout = 44444.seconds
      }.createConfigSummary().apply {
         this.shouldInclude("Default test timeout: 12ms")
         this.shouldInclude("Default test invocation timeout: 34234ms")
         this.shouldInclude("Overall project timeout: 12h 20m 44sms")
      }
   }

   xtest("dump should include affinity") {
      ProjectConfiguration().apply {
         timeout = 12
         invocationTimeout = 34234
      }.createConfigSummary().apply {
         this.shouldInclude("Dispatcher affinity: true")
      }
   }

   test("dump should include test order") {
      ProjectConfiguration().apply {
         testCaseOrder = TestCaseOrder.Random
      }.createConfigSummary().apply {
         this.shouldInclude("Default test execution order: Random")
      }
   }

   test("dump should include specExecutionMode") {
      ProjectConfiguration().apply {
         specExecutionMode = SpecExecutionMode.Concurrent
      }.createConfigSummary().apply {
         this.shouldInclude("Spec execution mode: Concurrent")
      }
   }

   test("dump should include testExecutionMode") {
      ProjectConfiguration().apply {
         testExecutionMode = TestExecutionMode.Concurrent
      }.createConfigSummary().apply {
         this.shouldInclude("Test execution mode: Concurrent")
      }
   }

   test("dump should include Spec execution order") {
      ProjectConfiguration().apply {
         specExecutionOrder = SpecExecutionOrder.Annotated
      }.createConfigSummary().apply {
         this.shouldInclude("Spec execution order: Annotated")
      }
   }

   test("dump should include Duplicate test name mode") {
      ProjectConfiguration().apply {
         duplicateTestNameMode = DuplicateTestNameMode.Silent
      }.createConfigSummary().apply {
         this.shouldInclude("Duplicate test name mode: Silent")
      }
   }

   test("dump should include default isolation mode") {
      ProjectConfiguration().apply {
         isolationMode = IsolationMode.InstancePerRoot
      }.createConfigSummary().apply {
         this.shouldInclude("Default isolation mode: InstancePerRoot")
      }
   }

   test("dump should include failOnEmptyTestSuite") {
      ProjectConfiguration().apply {
         failOnEmptyTestSuite = true
      }.createConfigSummary().apply {
         this.shouldInclude("Fail on empty test suite: true")
      }
   }

   test("dump should include coroutineDebugProbes") {
      ProjectConfiguration().apply {
         coroutineDebugProbes = true
      }.createConfigSummary().apply {
         this.shouldInclude("Coroutine debug probe: true")
      }
   }

   test("dump should include failOnIgnoredTests") {
      ProjectConfiguration().apply {
         failOnIgnoredTests = true
      }.createConfigSummary().apply {
         this.shouldInclude("Fail on ignored tests: true")
      }
   }

   test("dump should include globalAssertSoftly") {
      ProjectConfiguration().apply {
         globalAssertSoftly = true
      }.createConfigSummary().apply {
         this.shouldInclude("Global soft assertions: true")
      }
   }
})
