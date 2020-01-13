package io.kotest.core.spec.style

import io.kotest.core.*
import io.kotest.core.factory.TestFactory
import io.kotest.core.spec.SpecConfiguration
import io.kotest.core.spec.SpecDsl
import io.kotest.core.spec.TestFactoryConfiguration
import io.kotest.core.spec.build
import io.kotest.core.specs.KotestDsl
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.deriveTestConfig
import io.kotest.extensions.TestCaseExtension
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Creates a [TestFactory] from the given block.
 *
 * The receiver of the block is a [FunSpecTestFactoryConfiguration] which allows tests
 * to be defined using the 'fun-spec' style.
 */
fun funSpec(block: FunSpecTestFactoryConfiguration.() -> Unit): TestFactory {
   val config = FunSpecTestFactoryConfiguration()
   config.block()
   return config.build()
}

fun testcase(name: String, f: suspend (TestContext).() -> Unit): TestFactory =
    funSpec {
        test(name, f)
    }

class FunSpecTestFactoryConfiguration : TestFactoryConfiguration(),
    FunSpecDsl {
   override val addTest = ::addDynamicTest
}

abstract class FunSpec(body: FunSpec.() -> Unit = {}) : SpecConfiguration(),
    FunSpecDsl {
   override val addTest = ::addRootTestCase

   init {
      body()
   }
}

/**
 * Defines the DSL for creating tests in the 'FunSpec' style
 */
interface FunSpecDsl : SpecDsl {

   class TestBuilder(
      private val name: String,
      private val spec: FunSpecDsl
   ) {
      @UseExperimental(ExperimentalTime::class)
      fun config(
         enabled: Boolean? = null,
         tags: Set<Tag>? = null,
         timeout: Duration? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = spec.defaultTestCaseConfig.deriveTestConfig(enabled, tags, extensions)
         spec.addTest(name, test, config, TestType.Test)
      }
   }

   fun context(name: String, init: suspend ContextScope.() -> Unit) {
      addTest(name, { ContextScope(this, this@FunSpecDsl).init() }, defaultTestCaseConfig, TestType.Container)
   }

   @KotestDsl
   class ContextScope(private val context: TestContext, private val spec: FunSpecDsl) {

      suspend fun context(name: String, init: suspend ContextScope.() -> Unit) {
         context.registerTestCase(
            name,
            { ContextScope(this, this@ContextScope.spec).init() },
            spec.defaultTestCaseConfig,
            TestType.Container
         )
      }

      inner class TestBuilder(val name: String) {
         suspend fun config(
            enabled: Boolean? = null,
            tags: Set<Tag>? = null,
            extensions: List<TestCaseExtension>? = null,
            test: suspend TestContext.() -> Unit
         ) {
            val config = spec.defaultTestCaseConfig.deriveTestConfig(enabled, tags, extensions)
            context.registerTestCase(name, test, config, TestType.Test)
         }
      }

      fun test(name: String) = TestBuilder(name)

      suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(name, test, spec.defaultTestCaseConfig, TestType.Test)
   }

   fun test(name: String) = TestBuilder(name, this)

   /**
    * Adds a new root test case, with the given name and test function, using
    * the default test case config for this builder.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTest(name, test, defaultTestCaseConfig, TestType.Test)

}
