package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.*
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Defines the DSL for creating tests in the 'FunSpec' style
 */
@UseExperimental(ExperimentalTime::class)
interface DescribeSpecDsl : SpecDsl {

   @KotestDsl
   class TestBuilder(
      val context: TestContext,
      val name: String,
      val dsl: DescribeSpecDsl,
      private val xdisabled: Boolean? = null
   ) {

      suspend fun config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val active = if (xdisabled == true) false else enabled
         val config = dsl.defaultConfig().deriveTestConfig(active, tags, extensions, timeout)
         context.registerTestCase(name, test, config, TestType.Test)
      }
   }

   @KotestDsl
   class DescribeScope(val context: TestContext, val dsl: DescribeSpecDsl) {

      fun it(name: String) = TestBuilder(context, "It: $name", dsl, false)
      fun xit(name: String) = TestBuilder(context, "It: $name", dsl, true)

      suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(
            createTestName("It: ", name),
            test,
            dsl.defaultConfig(),
            TestType.Test
         )

      suspend fun xit(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(
            createTestName("It: ", name),
            test,
            dsl.defaultConfig().deriveTestConfig(enabled = false),
            TestType.Test
         )

      suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) =
         context.registerTestCase(
            createTestName("Context: ", name),
            { DescribeScope(this, this@DescribeScope.dsl).test() },
            dsl.defaultConfig(),
            TestType.Container
         )

      suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
         context.registerTestCase(
            createTestName("Describe: ", name),
            { DescribeScope(this, this@DescribeScope.dsl).test() },
            dsl.defaultConfig(),
            TestType.Container
         )

      suspend fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) =
         context.registerTestCase(
            createTestName("Describe: ", name),
            { DescribeScope(this, this@DescribeScope.dsl).test() },
            dsl.defaultConfig().deriveTestConfig(enabled = false),
            TestType.Container
         )
   }

   fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
      addTest(
         createTestName("Describe: ", name),
         { DescribeScope(this, this@DescribeSpecDsl).test() },
         defaultConfig(),
         TestType.Container
      )

   fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) =
      addTest(
         createTestName("Describe: ", name),
         { DescribeScope(this, this@DescribeSpecDsl).test() },
         defaultConfig().deriveTestConfig(enabled = false),
         TestType.Container
      )
}
