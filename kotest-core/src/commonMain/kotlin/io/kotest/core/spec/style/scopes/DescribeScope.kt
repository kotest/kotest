package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

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
   override val defaultConfig: TestCaseConfig
) : ContainerScope {

   suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name)
      addContainerTest(testName, xdisabled = false) {
         DescribeScope(
            this@DescribeScope.description.append(testName),
            this@DescribeScope.lifecycle,
            this,
            this@DescribeScope.defaultConfig
         ).test()
      }
   }

   suspend fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name)
      addContainerTest(testName, xdisabled = true) {
         DescribeScope(
            this@DescribeScope.description.append(testName),
            this@DescribeScope.lifecycle,
            this,
            this@DescribeScope.defaultConfig
         ).test()
      }
   }

   fun it(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = false)
   fun xit(name: String) = TestWithConfigBuilder(name, testContext, defaultConfig, xdisabled = true)

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) = addTest(name, xdisabled = false, test = test)
   suspend fun xit(name: String, test: suspend TestContext.() -> Unit) = addTest(name, xdisabled = true, test = test)
}
