package com.sksamuel.kotest.engine.extensions.spec.beforespec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.Isolate
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.extensions.MountableExtension
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.extensions.install
import io.kotest.core.listeners.BeforeSpecListener
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.datatest.withData
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.extensions.ExtensionException.BeforeSpecException
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.engine.listener.NoopTestEngineListener
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import java.util.concurrent.atomic.AtomicInteger

@EnabledIf(LinuxOnlyGithubCondition::class)
@Isolate
class BeforeSpecListenerTest : FunSpec() {
   init {

      beforeTest {
         counter.set(0)
      }

      test("BeforeSpecListener registered in project config should be triggered for a spec with tests") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyBeforeSpecListener)
         }

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecTests::class)
            .withProjectConfig(c)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener via method override should be triggered for a spec with tests") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecOverrideMethodTests::class)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener inline should be triggered for a spec with tests") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineTest::class)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener inline should be triggered before tests") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineOrderFunSpecTest::class)
            .launch()

         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineOrderDescribeSpecTest::class)
            .launch()

         a shouldBe "spectest1test2spectestinner"
      }

      test("BeforeSpecListener inline should be triggered before user level test interceptors") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecInlineWithTestInterceptor::class)
            .launch()

         b shouldBe "beforeSpecintercepttest"
      }

      test("BeforeSpecListener registered by overriding extensions should be triggered for a spec with tests") {

         val listener = CollectingTestEngineListener()
         TestEngineLauncher(listener)
            .withClasses(BeforeSpecByReturningExtensionsTest::class)
            .launch()

         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 2

         counter.get() shouldBe 1
      }

      test("BeforeSpecListener should NOT be triggered for a spec without tests") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyBeforeSpecListener)
         }

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecNoTests::class)
            .withProjectConfig(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("BeforeSpecListener should NOT be triggered for a spec without tests and handle errors in the listener") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyBeforeSpecListener)
         }

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecErrorNoTests::class)
            .withProjectConfig(c)
            .launch()

         counter.get() shouldBe 0
      }

      test("BeforeSpecListener should NOT be triggered for a spec with only ignored tests") {

         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(MyBeforeSpecListener)
         }

         TestEngineLauncher(NoopTestEngineListener)
            .withClasses(BeforeSpecDisabledOnlyTests::class)
            .withProjectConfig(c)
            .launch()

         counter.get() shouldBe 0
      }

      context("BeforeSpecListener error with SingleInstance should fail the spec; mark first test as error and the rest as ignored") {
         val listener = CollectingTestEngineListener()
         val config = object : AbstractProjectConfig() {
            override val isolationMode = IsolationMode.SingleInstance
         }
         TestEngineLauncher(listener)
            .withProjectConfig(config)
            .withClasses(BeforeSpecFunctionOverrideWithError::class)
            .launch()
         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 3
         listener.result("foo1")!!.errorOrNull.shouldBeInstanceOf<BeforeSpecException>()
         listener.result("foo2")!!.isIgnored shouldBe true
         listener.result("foo3")!!.isIgnored shouldBe true
      }

      context("BeforeSpecListener error with InstancePerRoot should fail the spec; mark all instances as failed") {
         val listener = CollectingTestEngineListener()
         val config = object : AbstractProjectConfig() {
            override val isolationMode = IsolationMode.InstancePerRoot
         }
         TestEngineLauncher(listener)
            .withProjectConfig(config)
            .withClasses(BeforeSpecFunctionOverrideWithError::class)
            .launch()
         listener.specs.size shouldBe 1
         listener.tests.size shouldBe 3
         listener.result("foo1")!!.errorOrNull.shouldBeInstanceOf<BeforeSpecException>()
         listener.result("foo2")!!.errorOrNull.shouldBeInstanceOf<BeforeSpecException>()
         listener.result("foo3")!!.errorOrNull.shouldBeInstanceOf<BeforeSpecException>()
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

   override val extensions: List<Extension> = listOf(MyBeforeSpecListener)

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

private object BrokenExtension : MountableExtension<Unit, Unit>, BeforeSpecListener {
   const val DUMMY_ERROR_MESSAGE = "should fail!"
   override fun mount(configure: Unit.() -> Unit) {}

   override suspend fun beforeSpec(spec: Spec) {
      error(DUMMY_ERROR_MESSAGE)
   }
}

private class BeforeSpecErrorInstancePerRoot : DescribeSpec({
   isolationMode = IsolationMode.InstancePerRoot

   install(BrokenExtension)

   it("it should fail") { // this test is not even run
      true.shouldBeFalse()
   }
})

private class BeforeSpecFunctionOverrideWithError : FunSpec() {

   override suspend fun beforeSpec(spec: Spec) {
      error("boom")
   }

   init {
      test("foo1") {}
      test("foo2") {}
      test("foo3") {}
   }
}
