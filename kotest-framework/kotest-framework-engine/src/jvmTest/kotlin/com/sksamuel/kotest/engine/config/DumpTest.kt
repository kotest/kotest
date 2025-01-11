package com.sksamuel.kotest.engine.config

import io.kotest.core.annotation.Isolate
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.names.DuplicateTestNameMode
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCaseOrder
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.concurrency.TestExecutionMode
import io.kotest.engine.config.AbstractProjectConfigWriter
import io.kotest.matchers.string.shouldInclude
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Isolate
class DumpTest : FunSpec({

   test("dump should include test timeouts") {
      val c = object : AbstractProjectConfig() {
         override val timeout = 12.milliseconds
         override val projectTimeout = 44444.seconds
         override val invocationTimeout = 34234.milliseconds
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Default test timeout: 12ms")
         this.shouldInclude("Default test invocation timeout: 34.234s")
         this.shouldInclude("Project timeout: 12h 20m 44s")
      }
   }

   test("dump should include test order") {
      val c = object : AbstractProjectConfig() {
         override val testCaseOrder = TestCaseOrder.Random
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Test case order: Random")
      }
   }

   test("dump should include specExecutionMode") {
      val c = object : AbstractProjectConfig() {
         override val specExecutionMode = SpecExecutionMode.Concurrent
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Spec execution mode: Concurrent")
      }
   }

   test("dump should include testExecutionMode") {
      val c = object : AbstractProjectConfig() {
         override val testExecutionMode = TestExecutionMode.Concurrent
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Test execution mode: Concurrent")
      }
   }

   test("dump should include Spec execution order") {
      val c = object : AbstractProjectConfig() {
         override val specExecutionOrder = SpecExecutionOrder.Annotated
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Spec execution order: Annotated")
      }
   }

   test("dump should include Duplicate test name mode") {
      val c = object : AbstractProjectConfig() {
         override val duplicateTestNameMode = DuplicateTestNameMode.Silent
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Duplicate test name mode: Silent")
      }
   }

   test("dump should include default isolation mode") {
      val c = object : AbstractProjectConfig() {
         override val isolationMode = IsolationMode.InstancePerRoot
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Default isolation mode: InstancePerRoot")
      }
   }

   test("dump should include failOnEmptyTestSuite") {
      val c = object : AbstractProjectConfig() {
         override val failOnEmptyTestSuite = true
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Fail on empty test suite: true")
      }
   }

   test("dump should include coroutineDebugProbes") {
      val c = object : AbstractProjectConfig() {
         override val coroutineDebugProbes = true
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Coroutine debug probe: true")
      }
   }

   test("dump should include failOnIgnoredTests") {
      val c = object : AbstractProjectConfig() {
         override val failOnIgnoredTests = true
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Fail on ignored tests: true")
      }
   }

   test("dump should include globalAssertSoftly") {
      val c = object : AbstractProjectConfig() {
         override val globalAssertSoftly = true
      }
      AbstractProjectConfigWriter.createConfigSummary(c).apply {
         this.shouldInclude("Global soft assertions: true")
      }
   }
})
