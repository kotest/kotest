package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.DumpConfigInterceptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.extensions.system.SystemOutWireListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty

@EnabledIf(LinuxCondition::class)
class DumpConfigInterceptorTest : FunSpec({

   val property = "kotest.framework.dump.config"

   val sysOutListener = SystemOutWireListener()
   extension(sysOutListener)

   beforeEach {
      System.clearProperty(property)
   }

   context("Uses system property `$property` correctly") {
      val configuration = ProjectConfiguration()
      val engineContext = EngineContext.empty.copy(configuration = configuration)

      withData(
         "true",
         "TRUE",
         "True",
      ) { propValue ->
         System.setProperty(property, propValue)
         DumpConfigInterceptor.intercept(engineContext) { t -> EngineResult(emptyList()) }
         sysOutListener.output() shouldBe """
            |~~~ Kotest Configuration ~~~
            |-> Parallelization factor: 1
            |-> Concurrent specs: null
            |-> Global concurrent tests: 1
            |-> Dispatcher affinity: true
            |-> Coroutine debug probe: false
            |-> Spec execution order: Lexicographic
            |-> Default test execution order: Sequential
            |-> Default test timeout: 600000ms
            |-> Default test invocation timeout: 600000ms
            |-> Default isolation mode: SingleInstance
            |-> Global soft assertions: false
            |-> Write spec failure file: false
            |-> Fail on ignored tests: false
            |-> Fail on empty test suite: false
            |-> Duplicate test name mode: Warn
            |-> Remove test name whitespace: false
            |-> Append tags to test names: false
            |-> ${"Tags: "}
            |
            |
         """.trimMargin()
         // "Tags: " escaped to avoid formatter trimming whitespace at end of line which exists in actual output.
      }

      test("No property set, dumps nothing") {
         DumpConfigInterceptor.intercept(engineContext) { t -> EngineResult(emptyList()) }
         sysOutListener.output().shouldBeEmpty()
      }

      withData(
         "not_true",
         "false",
         "FALSE",
         "Anything really"
      ) { propValue ->
         System.setProperty(property, propValue)
         DumpConfigInterceptor.intercept(engineContext) { t -> EngineResult(emptyList()) }
         sysOutListener.output().shouldBeEmpty()
      }
   }
})
