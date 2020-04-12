package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import io.kotest.core.spec.SpecDsl
import io.kotest.core.test.Description
import io.kotest.core.test.EnabledIf
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName
import io.kotest.core.test.deriveTestConfig
import io.kotest.fp.Tuple2
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

object TestBuilders {
   var state: String? = null
}

/**
 * Defines the DSL for creating tests in the 'FunSpec' style
 */
@OptIn(ExperimentalTime::class)
interface DescribeSpecDsl : SpecDsl {

   @KotestDsl
   class TestBuilder(
      val context: TestContext,
      val name: String,
      val dsl: DescribeSpecDsl,
      private val xdisabled: Boolean? = null
   ) {

      init {
         if (TestBuilders.state != null)
            error("Cannot invoke 'it' here as a previous 'it' scope was not completed")
         TestBuilders.state = name
      }

      suspend fun config(
         enabled: Boolean? = null,
         invocations: Int? = null,
         threads: Int? = null,
         tags: Set<Tag>? = null,
         timeout: Duration? = null,
         extensions: List<TestCaseExtension>? = null,
         enabledIf: EnabledIf? = null,
         test: suspend TestContext.() -> Unit
      ) {
         TestBuilders.state = null
         val active = if (xdisabled == true) false else enabled
         val config = dsl.defaultConfig()
            .deriveTestConfig(active, tags, extensions, timeout, enabledIf, invocations, threads)
         context.registerTestCase(name, test, config, TestType.Test)
      }
   }

   @KotestDsl
   class DescribeScope(
      private val description: Description,
      private val context: TestContext,
      private val spec: DescribeSpecDsl
   ) {

      fun beforeTest(f: BeforeTest) {
         spec.addListener(object : TestListener {
            override suspend fun beforeTest(testCase: TestCase) {
               if (description.isParentOf(testCase.description)) f(testCase)
            }
         })
      }

      fun afterTest(f: AfterTest) {
         spec.addListener(object : TestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               if (description.isParentOf(testCase.description)) f(Tuple2(testCase, result))
            }
         })
      }

      fun it(name: String) = TestBuilder(context, "It: $name", spec, false)
      fun xit(name: String) = TestBuilder(context, "It: $name", spec, true)

      suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(
            createTestName("It: ", name),
            test,
            spec.defaultConfig(),
            TestType.Test
         )

      suspend fun xit(name: String, test: suspend TestContext.() -> Unit) {
         val testName = createTestName("It: ", name)
         context.registerTestCase(
            testName,
            test,
            spec.defaultConfig().deriveTestConfig(enabled = false),
            TestType.Test
         )
      }

      suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) {
         val testName = createTestName("Context: ", name)
         context.registerTestCase(
            testName,
            { DescribeScope(this@DescribeScope.description.append(testName), this, this@DescribeScope.spec).test() },
            spec.defaultConfig(),
            TestType.Container
         )
      }

      suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
         val testName = createTestName("Describe: ", name)
         context.registerTestCase(
            testName,
            { DescribeScope(this@DescribeScope.description.append(testName), this, this@DescribeScope.spec).test() },
            spec.defaultConfig(),
            TestType.Container
         )
      }

      suspend fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
         val testName = createTestName("Describe: ", name)
         context.registerTestCase(
            testName,
            { DescribeScope(this@DescribeScope.description.append(testName), this, this@DescribeScope.spec).test() },
            spec.defaultConfig().deriveTestConfig(enabled = false),
            TestType.Container
         )
      }
   }

   fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name)
      addTest(
         testName,
         {
            DescribeScope(
               Description.specUnsafe(this@DescribeSpecDsl).append(testName),
               this,
               this@DescribeSpecDsl
            ).test()
         },
         defaultConfig(),
         TestType.Container
      )
   }

   fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name)
      addTest(
         testName,
         {
            DescribeScope(
               Description.specUnsafe(this@DescribeSpecDsl).append(testName),
               this,
               this@DescribeSpecDsl
            ).test()
         },
         defaultConfig().deriveTestConfig(enabled = false),
         TestType.Container
      )
   }
}
