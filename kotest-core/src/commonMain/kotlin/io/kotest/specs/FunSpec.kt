package io.kotest.specs

import io.kotest.Tag
import io.kotest.TestCase
import io.kotest.TestType
import io.kotest.core.TestCaseConfig
import io.kotest.core.TestContext
import io.kotest.core.specs.AbstractSpecDsl
import io.kotest.core.specs.KotestDsl
import io.kotest.extensions.TestCaseExtension
import io.kotest.shouldBe
import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

interface FunSpecCollector {
   fun test(name: String)
   fun test(name: String, test: suspend TestContext.() -> Unit)
}

open class SafeFunSpecCollector : FunSpecCollector {

   internal val rootTestCases = mutableListOf<TestCase>()

   override fun test(name: String) {
   }

   override fun test(name: String, test: suspend TestContext.() -> Unit) {
   }
}

fun funSpec(f: FunSpecCollector.() -> Unit): List<TestCase> {
   val collector = SafeFunSpecCollector()
   collector.f()
   return collector.rootTestCases
}

val myTests = funSpec {

   test("my test") {
      delay(10)
      1 shouldBe 2
   }

   test("my test") {
      delay(10)
      1 shouldBe 2
   }
}

abstract class SuperFunSpec(body: FunSpecCollector.() -> Unit = {}) : SafeFunSpecCollector() {

   init {
      body()
   }

   fun include(tests: List<TestCase>) {

   }
}

class MyCompositeFunSpec : SuperFunSpec() {
   init {
      test("my test") {
         delay(10)
         1 shouldBe 2
      }
      include(myTests)
   }
}

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : AbstractSpecDsl() {

   init {
      body()
   }

   inner class TestBuilder(val name: String) {
      @UseExperimental(ExperimentalTime::class)
      fun config(
         invocations: Int? = null,
         enabled: Boolean? = null,
         timeout: Duration? = null,
         parallelism: Int? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = TestCaseConfig(
            enabled ?: defaultTestCaseConfig.enabled,
            invocations ?: defaultTestCaseConfig.invocations,
            timeout ?: defaultTestCaseConfig.timeout,
            parallelism ?: defaultTestCaseConfig.threads,
            tags ?: defaultTestCaseConfig.tags,
            extensions ?: defaultTestCaseConfig.extensions
         )
         addTestCase(name, test, config, TestType.Test)
      }
   }

   fun context(name: String, init: suspend ContextScope.() -> Unit) {
      addTestCase(name, { ContextScope(this).init() }, defaultTestCaseConfig, TestType.Container)
   }

   @KotestDsl
   inner class ContextScope(val context: TestContext) {

      suspend fun context(name: String, init: suspend ContextScope.() -> Unit) {
         context.registerTestCase(
            name,
            this@FunSpec,
            { ContextScope(this).init() },
            defaultTestCaseConfig,
            TestType.Container
         )
      }

      inner class TestBuilder(val name: String) {
         @UseExperimental(ExperimentalTime::class)
         suspend fun config(
            invocations: Int? = null,
            enabled: Boolean? = null,
            timeout: Duration? = null,
            parallelism: Int? = null,
            tags: Set<Tag>? = null,
            extensions: List<TestCaseExtension>? = null,
            test: suspend TestContext.() -> Unit
         ) {
            val config = TestCaseConfig(
               enabled ?: defaultTestCaseConfig.enabled,
               invocations ?: defaultTestCaseConfig.invocations,
               timeout ?: defaultTestCaseConfig.timeout,
               parallelism ?: defaultTestCaseConfig.threads,
               tags ?: defaultTestCaseConfig.tags,
               extensions ?: defaultTestCaseConfig.extensions
            )
            context.registerTestCase(name, this@FunSpec, test, config, TestType.Test)
         }
      }

      fun test(name: String) = TestBuilder(name)

      suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(name, this@FunSpec, test, defaultTestCaseConfig, TestType.Test)
   }

   fun test(name: String) = TestBuilder(name)

   fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTestCase(name, test, defaultTestCaseConfig, TestType.Test)
}
