package com.sksamuel.kotest.engine.interceptors

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.engine.EngineResult
import io.kotest.engine.interceptors.DumpProjectConfigInterceptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.extensions.system.SystemOutWireListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty

@EnabledIf(LinuxOnlyGithubCondition::class)
class DumpConfigInterceptorTest : FunSpec({

   val property = "kotest.framework.dump.config"

   val sysOutListener = SystemOutWireListener()
   extension(sysOutListener)

   beforeEach {
      System.clearProperty(property)
   }

   context("Uses system property `$property` correctly") {
      val engineContext = EngineContext.empty.withProjectConfig(object : AbstractProjectConfig() {
         override val globalAssertSoftly = true
         override val specExecutionOrder = SpecExecutionOrder.Annotated
      })

      withData(
         "true",
         "TRUE",
         "True",
      ) { propValue ->
         System.setProperty(property, propValue)
         DumpProjectConfigInterceptor.intercept(engineContext) { t -> EngineResult(emptyList()) }
         sysOutListener.output().trim() shouldBe """
            |~~~ Kotest Configuration ~~~
            |-> Spec execution order: Annotated
            |-> Global soft assertions: true
         """.trimMargin().trim()
         // "Tags: " escaped to avoid formatter trimming whitespace at end of line which exists in actual output.
      }

      test("No property set, dumps nothing") {
         DumpProjectConfigInterceptor.intercept(engineContext) { t -> EngineResult(emptyList()) }
         sysOutListener.output().shouldBeEmpty()
      }

      withData(
         "not_true",
         "false",
         "FALSE",
         "Anything really"
      ) { propValue ->
         System.setProperty(property, propValue)
         DumpProjectConfigInterceptor.intercept(engineContext) { t -> EngineResult(emptyList()) }
         sysOutListener.output().shouldBeEmpty()
      }
   }
})
