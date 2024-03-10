package com.sksamuel.kotest.engine.extensions.spec

import io.kotest.core.annotation.Isolate
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.atomic.AtomicInteger

@Isolate
class BeforeSpecListenerTest : FunSpec() {
   init {

      beforeTest {
         counter.set(0)
      }

      test("BeforeSpecListener registered in project config should be triggered for a spec with tests") {

         val c = ProjectConfiguration()
         c.registry.add(MyBeforeSpecListener)
         c.includePrivateClasses = true

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecTests::class)
            .withConfiguration(c)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener via method override should be triggered for a spec with tests") {

         val c = ProjectConfiguration()
         c.includePrivateClasses = true

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecOverrideMethodTests::class)
            .withConfiguration(c)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener inline should be triggered for a spec with tests") {

         val c = ProjectConfiguration()
         c.includePrivateClasses = true

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineTest::class)
            .withConfiguration(c)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener inline should be triggered before tests") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineOrderFunSpecTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()

         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineOrderDescribeSpecTest::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()

         a shouldBe "spectest1test2spectestinner"
      }

      test("BeforeSpecListener inline should be triggered before user level test interceptors") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineWithTestInterceptor::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()

         b shouldBe "beforeSpecintercepttest"
      }

      test("BeforeSpecListener registered by overriding extensions should be triggered for a spec with tests") {

         val c = ProjectConfiguration()
         c.includePrivateClasses = true

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecByReturningExtensionsTest::class)
            .withConfiguration(c)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener should NOT be triggered for a spec without tests") {

         val c = ProjectConfiguration()
         c.registry.add(MyBeforeSpecListener)
         c.includePrivateClasses = true

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecNoTests::class)
            .withConfiguration(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("BeforeSpecListener should NOT be triggered for a spec without tests and handle errors in the listener") {

         val c = ProjectConfiguration()
         c.registry.add(MyBeforeSpecListener)
         c.includePrivateClasses = true

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecErrorNoTests::class)
            .withConfiguration(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("BeforeSpecListener should NOT be triggered for a spec with only ignored tests") {

         val c = ProjectConfiguration()
         c.registry.add(MyBeforeSpecListener)
         c.includePrivateClasses = true

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecDisabledOnlyTests::class)
            .withConfiguration(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("BeforeSpecListener exceptions should be propagated and further tests skipped") {
         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecWithError::class)
            .withConfiguration(ProjectConfiguration().also { it.includePrivateClasses = true })
            .launch()
         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 3
         listener.result("foo1")!!.errorOrNull.shouldBeInstanceOf<ExtensionException.BeforeSpecException>()
         listener.result("foo2")!!.isIgnored shouldBe true
         listener.result("foo3")!!.isIgnored shouldBe true
      }
   }
}

private val counter = AtomicInteger(0)

private object MyBeforeSpecListener : BeforeSpecListener {
   override suspend fun beforeSpec(spec: Spec) {
      counter.incrementAndGet()
   }
}

private class BeforeSpecTests : FunSpec() {
   init {
      test("foo1") {}
      test("foo2") {}
   }
}

private class BeforeSpecOverrideMethodTests : FunSpec() {
   override suspend fun beforeSpec(spec: Spec) {
      counter.incrementAndGet()
   }

   init {
      test("foo1") {}
      test("foo2") {}
   }
}

private class BeforeSpecInlineTest : FunSpec() {
   init {
      beforeSpec { counter.incrementAndGet() }
      test("foo1") {}
      test("foo2") {}
   }
}


private var a = ""

private class BeforeSpecInlineOrderFunSpecTest : FunSpec() {
   init {
      beforeSpec { a += "spec" }
      test("test1") { a += "test1" }
      test("test2") { a += "test2" }
   }
}

private var b = ""

private class BeforeSpecInlineWithTestInterceptor : FunSpec() {
   init {
      extension(object : TestCaseExtension {
         override suspend fun intercept(testCase: TestCase, execute: suspend (TestCase) -> TestResult): TestResult {
            b += "intercept"
            return execute(testCase)
         }
      })
      beforeSpec { b += "beforeSpec" }
      test("test") { b += "test" }
   }
}

private class BeforeSpecInlineOrderDescribeSpecTest : DescribeSpec() {
   init {
      beforeSpec { a += "spec" }
      describe("test") {
         a += "test"
         it("inner") {
            a += "inner"
         }
      }
   }
}

private class BeforeSpecByReturningExtensionsTest : FunSpec() {

   override fun extensions(): List<Extension> {
      return listOf(MyBeforeSpecListener)
   }

   init {
      test("foo1") {}
      test("foo2") {}
   }
}

private class BeforeSpecNoTests : FunSpec()

private class BeforeSpecErrorNoTests : FunSpec() {
   override suspend fun beforeSpec(spec: Spec) {
      error("boom")
   }
}

private class BeforeSpecDisabledOnlyTests : FunSpec() {
   init {
      test("disabled by config").config(enabled = false) {}
      xtest("disabled by xmethod") { }
   }
}


private class BeforeSpecWithError : FunSpec() {
   override suspend fun beforeSpec(spec: Spec) {
      error("boom")
   }

   init {
      test("foo1") {}
      test("foo2") {}
      test("foo3") {}
   }
}
