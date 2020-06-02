package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

/**
 * A context that allows tests to be registered using the syntax:
 *
 * context("some test")
 * xcontext("some disabled test")
 *
 * and
 *
 * expect("some test")
 * expect("some test").config(...)
 * xexpect("some test")
 * xexpect("some test").config(...)
 *
 */
@KotestDsl
class ExpectScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig
) : ContainerScope {

   suspend fun context(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = createTestName("Context: ", name)
      addContainerTest(testName, xdisabled = false) {
         ExpectScope(
            this@ExpectScope.description.append(testName),
            this@ExpectScope.lifecycle,
            this,
            this@ExpectScope.defaultConfig
         ).test()
      }
   }

   suspend fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = createTestName("Context: ", name)
      addContainerTest(testName, xdisabled = true) {
         ExpectScope(
            this@ExpectScope.description.append(testName),
            this@ExpectScope.lifecycle,
            this,
            this@ExpectScope.defaultConfig
         ).test()
      }
   }

   suspend fun expect(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName("Expect: ", name)
      addTest(testName, xdisabled = false, test = test)
   }

   suspend fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName("Expect: ", name)
      addTest(testName, xdisabled = true, test = test)
   }

   fun expect(name: String) =
      TestWithConfigBuilder(createTestName("Expect: ", name), testContext, defaultConfig, xdisabled = false)

   fun xexpect(name: String) =
      TestWithConfigBuilder(createTestName("Expect: ", name), testContext, defaultConfig, xdisabled = true)

}
