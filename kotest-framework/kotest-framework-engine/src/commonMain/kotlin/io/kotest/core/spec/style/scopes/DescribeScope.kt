package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionType
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName
import kotlin.coroutines.CoroutineContext

/**
 * A context that allows tests to be registered using the syntax:
 *
 * describe("some test")
 * xdescribe("some disabled test")
 *
 * and
 *
 * it("some test")
 * it("some test").config(...)
 * xit("some test")
 * xit("some test").config(...)
 */
@KotestDsl
class DescribeScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Context: ", name)
      containerTest(testName, false, test)
   }

   suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Describe: ", name)
      containerTest(testName, false, test)
   }

   suspend fun xcontext(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Context: ", name)
      containerTest(testName, true, test)
   }

   suspend fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Describe: ", name)
      containerTest(testName, true, test)
   }

   private suspend fun containerTest(
      testName: TestName,
      xdisabled: Boolean,
      test: suspend DescribeScope.() -> Unit
   ) {
      addContainerTest(testName, xdisabled = xdisabled) {
         DescribeScope(
            this@DescribeScope.description.append(testName, DescriptionType.Container),
            this@DescribeScope.lifecycle,
            this,
            this@DescribeScope.defaultConfig,
            this@DescribeScope.coroutineContext,
         ).test()
      }
   }

   fun it(name: String) =
      TestWithConfigBuilder(
         TestName("It: ", name),
         testContext,
         defaultConfig,
         xdisabled = false,
      )

   fun xit(name: String) =
      TestWithConfigBuilder(
         TestName("It: ", name),
         testContext,
         defaultConfig,
         xdisabled = true,
      )

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
      addTest(TestName(name), xdisabled = false, test = test)

   suspend fun xit(name: String, test: suspend TestContext.() -> Unit) =
      addTest(TestName(name), xdisabled = true, test = test)
}
