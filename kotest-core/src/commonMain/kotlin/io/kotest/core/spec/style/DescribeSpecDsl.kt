package io.kotest.core.spec.style

import io.kotest.core.Tag
import io.kotest.core.extensions.TestCaseExtension
import io.kotest.core.spec.SpecDsl
import io.kotest.core.specs.KotestDsl
import io.kotest.core.test.createTestName
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Defines the DSL for creating tests in the 'FunSpec' style
 */
@UseExperimental(ExperimentalTime::class)
interface DescribeSpecDsl : SpecDsl {

   @KotestDsl
   class TestBuilder(val context: TestContext, val name: String, val dsl: DescribeSpecDsl) {

      suspend fun config(
         enabled: Boolean? = null,
         timeout: Duration? = null,
         tags: Set<Tag>? = null,
         extensions: List<TestCaseExtension>? = null,
         test: suspend TestContext.() -> Unit
      ) {
         val config = TestCaseConfig(
            enabled = enabled ?: dsl.defaultTestCaseConfig.enabled,
            timeout = timeout ?: dsl.defaultTestCaseConfig.timeout,
            tags = tags ?: dsl.defaultTestCaseConfig.tags,
            extensions = extensions ?: dsl.defaultTestCaseConfig.extensions
         )
         context.registerTestCase(name, test, config, TestType.Test)
      }
   }

   @KotestDsl
   class DescribeScope(val context: TestContext, val dsl: DescribeSpecDsl) {

      fun it(name: String) = TestBuilder(context, "It: $name", dsl)

      suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
         context.registerTestCase(
            createTestName("It: ", name),
            test,
            dsl.defaultTestCaseConfig,
            TestType.Test
         )

      suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) =
         context.registerTestCase(
            createTestName("Context: ", name),
            { DescribeScope(this, this@DescribeScope.dsl).test() },
            dsl.defaultTestCaseConfig,
            TestType.Container
         )

      suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
         context.registerTestCase(
            createTestName("Describe: ", name),
            { DescribeScope(this, this@DescribeScope.dsl).test() },
            dsl.defaultTestCaseConfig,
            TestType.Container
         )
   }

   fun describe(name: String, test: suspend DescribeScope.() -> Unit) =
      addTest(
         createTestName("Describe: ", name),
         { DescribeScope(this, this@DescribeSpecDsl).test() },
         defaultTestCaseConfig,
         TestType.Container
      )


}
