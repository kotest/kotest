package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName
import io.kotest.core.test.toTestContainerConfig
import kotlin.coroutines.CoroutineContext

/**
 * A scope that allows tests to be registered using the syntax:
 *
 * context("some context")
 * should("some test")
 * should("some test").config(...)
 *
 */
@KotestDsl
class ShouldSpecContextScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   override suspend fun addTest(name: String, test: suspend TestContext.() -> Unit) {
      should(name, test)
   }

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      val testName = createTestName(name)
      addContainerTest(testName, xdisabled = false) {
         ShouldSpecContextScope(
            this@ShouldSpecContextScope.description.appendContainer(testName),
            this@ShouldSpecContextScope.lifecycle,
            this,
            this@ShouldSpecContextScope.defaultConfig,
            this@ShouldSpecContextScope.coroutineContext,
         ).test()
      }
   }

   fun context(name: String) =
      ShouldSpecContextConfigBuilder(
         createTestName(name),
         description,
         testContext,
         defaultConfig.toTestContainerConfig(),
         lifecycle,
         false
      )

   suspend fun xcontext(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      val testName = createTestName(name)
      addContainerTest(testName, xdisabled = true) {
         ShouldSpecContextScope(
            this@ShouldSpecContextScope.description.appendContainer(testName),
            this@ShouldSpecContextScope.lifecycle,
            this,
            this@ShouldSpecContextScope.defaultConfig,
            this@ShouldSpecContextScope.coroutineContext,
         ).test()
      }
   }

   fun xcontext(name: String) =
      ShouldSpecContextConfigBuilder(
         createTestName(name),
         description,
         testContext,
         defaultConfig.toTestContainerConfig(),
         lifecycle,
         true
      )

   fun should(name: String) =
      TestWithConfigBuilder(
         createTestName("should ", name, false),
         testContext,
         defaultConfig,
         xdisabled = false,
      )

   fun xshould(name: String) =
      TestWithConfigBuilder(
         createTestName("should ", name, false),
         testContext,
         defaultConfig,
         xdisabled = true,
      )

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName("should ", name, true), xdisabled = false, test = test)

   suspend fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName("should ", name, true), xdisabled = true, test = test)
}
