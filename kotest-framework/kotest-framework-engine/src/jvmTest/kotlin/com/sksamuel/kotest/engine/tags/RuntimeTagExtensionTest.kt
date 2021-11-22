package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.fail
import io.kotest.core.Tag
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.RuntimeTagExpressionExtension
import io.kotest.core.extensions.RuntimeTagExtension
import io.kotest.core.annotation.Isolate
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.config.MutableConfiguration
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.collections.shouldBeEmpty

object MyRuntimeExcludedTag : Tag()

@Isolate
class RuntimeTagExtensionTest : StringSpec() {
   init {

      "Tests with tag should not execute when excluded by a RuntimeTagExtension" {
         val c = MutableConfiguration()
         c.registry().add(RuntimeTagExtension(included = emptySet(), excluded = setOf(MyRuntimeExcludedTag)))
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestWithTag::class)
            .launch()
            .errors.shouldBeEmpty()
      }

      "Tests with tag should not execute when excluded by a RuntimeTagExpressionExtension" {
         val c = MutableConfiguration()
         c.registry().add(RuntimeTagExpressionExtension("!MyRuntimeExcludedTag"))
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestWithTag::class)
            .launch()
            .errors.shouldBeEmpty()
      }
   }
}

private class TestWithTag : FunSpec() {
   init {
      test("Test marked with a runtime excluded tag").config(tags = setOf(MyRuntimeExcludedTag)) {
         fail("boom")
      }
   }
}
