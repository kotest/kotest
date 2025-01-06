package com.sksamuel.kotest.engine.tags

import io.kotest.assertions.fail
import io.kotest.core.Tag
import io.kotest.engine.tags.TagExpression
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.RuntimeTagExpressionExtension
import io.kotest.core.extensions.RuntimeTagExtension
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.extensions.TagExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import java.util.concurrent.atomic.AtomicInteger

object MyRuntimeExcludedTag : Tag()
object FooTag : Tag()
object FooTagExtension : TagExtension {
   override fun tags(): TagExpression = TagExpression.exclude(FooTag)
}

@Isolate
@EnabledIf(LinuxCondition::class)
class RuntimeTagExtensionTest : StringSpec() {
   init {

      "Tests with tag should not execute when excluded by a RuntimeTagExtension" {
         val c = ProjectConfiguration()
         c.registry.add(RuntimeTagExtension(included = emptySet(), excluded = setOf(MyRuntimeExcludedTag)))
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestWithTag::class)
            .withConfiguration(c)
            .launch()
            .errors.shouldBeEmpty()
      }

      "Tests with tag should not execute when excluded by a RuntimeTagExpressionExtension" {
         val c = ProjectConfiguration()
         c.registry.add(RuntimeTagExpressionExtension("!MyRuntimeExcludedTag"))
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestWithTag::class)
            .withConfiguration(c)
            .launch()
            .errors.shouldBeEmpty()
      }

      "tags defined in spec should stop listeners firing" {
         val c = ProjectConfiguration()
         c.registry.add(FooTagExtension)
         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(TestWithListenerAndTag::class)
            .withConfiguration(c)
            .launch()
         counter.get() shouldBe 0
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

val counter = AtomicInteger(0)

private class TestWithListenerAndTag : FunSpec() {
   init {
      tags(FooTag)
      extension(object : TestListener {
         override suspend fun beforeSpec(spec: Spec) {
            counter.incrementAndGet()
         }

         override suspend fun afterTest(testCase: TestCase, result: TestResult) {
            counter.incrementAndGet()
         }
      })
      test("foo") {}
   }
}
