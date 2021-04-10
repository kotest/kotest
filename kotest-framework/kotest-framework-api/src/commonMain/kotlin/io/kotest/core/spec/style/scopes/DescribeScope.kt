package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import io.kotest.core.test.toTestContainerConfig
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

   override suspend fun addTest(name: String, test: suspend TestContext.() -> Unit) {
      it(name, test)
   }

   suspend fun context(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      containerTest(testName, false, test)
   }

   fun context(name: String) = DescribeSpecScopeConfigBuilder(
      createTestName(name),
      description,
      testContext,
      defaultConfig.toTestContainerConfig(),
      lifecycle,
      false
   )

   suspend fun xcontext(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      containerTest(testName, true, test)
   }

   fun xcontext(name: String) = DescribeSpecScopeConfigBuilder(
      createTestName(name),
      description,
      testContext,
      defaultConfig.toTestContainerConfig(),
      lifecycle,
      true
   )

   suspend fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      containerTest(testName, false, test)
   }

   fun describe(name: String) = DescribeSpecScopeConfigBuilder(
      createTestName(name),
      description,
      testContext,
      defaultConfig.toTestContainerConfig(),
      lifecycle,
      false
   )

   suspend fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      containerTest(testName, true, test)
   }

   fun xdescribe(name: String) = DescribeSpecScopeConfigBuilder(
      createTestName(name),
      description,
      testContext,
      defaultConfig.toTestContainerConfig(),
      lifecycle,
      true
   )

   private suspend fun containerTest(
      testName: DescriptionName.TestName,
      xdisabled: Boolean,
      test: suspend DescribeScope.() -> Unit,
   ) {
      addContainerTest(testName, xdisabled = xdisabled) {
         DescribeScope(
            this@DescribeScope.description.appendContainer(testName),
            this@DescribeScope.lifecycle,
            this,
            this@DescribeScope.defaultConfig,
            this@DescribeScope.coroutineContext,
         ).test()
      }
   }

   suspend fun it(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName(name), xdisabled = false, test = test)

   fun it(name: String) =
      TestWithConfigBuilder(
         createTestName("It: ", name, false),
         testContext,
         defaultConfig,
         xdisabled = false,
      )

   suspend fun xit(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName(name), xdisabled = true, test = test)

   fun xit(name: String) =
      TestWithConfigBuilder(
         createTestName("It: ", name, false),
         testContext,
         defaultConfig,
         xdisabled = true,
      )
}
