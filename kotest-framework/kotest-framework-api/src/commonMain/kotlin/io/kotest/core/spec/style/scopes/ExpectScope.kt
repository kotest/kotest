package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import kotlin.coroutines.CoroutineContext

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
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   suspend fun context(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      addContainerTest(testName, xdisabled = false) {
         ExpectScope(
            this@ExpectScope.description.appendContainer(testName),
            this@ExpectScope.lifecycle,
            this,
            this@ExpectScope.defaultConfig,
            this@ExpectScope.coroutineContext,
         ).test()
      }
   }

   suspend fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      addContainerTest(testName, xdisabled = true) {
         ExpectScope(
            this@ExpectScope.description.appendContainer(testName),
            this@ExpectScope.lifecycle,
            this,
            this@ExpectScope.defaultConfig,
            this@ExpectScope.coroutineContext,
         ).test()
      }
   }

   suspend fun expect(name: String, test: suspend TestContext.() -> Unit) {
      addTest(createTestName("Expect: ", name, false), xdisabled = false, test = test)
   }

   suspend fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      addTest(createTestName("Expect: ", name, false), xdisabled = true, test = test)
   }

   fun expect(name: String): TestWithConfigBuilder {
      return TestWithConfigBuilder(
         createTestName("Expect: ", name, false),
         testContext,
         defaultConfig,
         xdisabled = false,
      )
   }

   fun xexpect(name: String): TestWithConfigBuilder {
      return TestWithConfigBuilder(
         createTestName("Expect: ", name, false),
         testContext,
         defaultConfig,
         xdisabled = true,
      )
   }

}
