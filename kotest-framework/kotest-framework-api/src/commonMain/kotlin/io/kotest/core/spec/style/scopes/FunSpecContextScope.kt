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
 * test("some test")
 * test("some test").config(...)
 *
 */
@KotestDsl
class FunSpecContextScope(
   override val description: Description,
   override val lifecycle: Lifecycle,
   override val testContext: TestContext,
   override val defaultConfig: TestCaseConfig,
   override val coroutineContext: CoroutineContext,
) : ContainerScope {

   /**
    * Adds a container scope to this scope.
    */
   suspend fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      val testName = createTestName(name)
      addContainerTest(testName, xdisabled = false) {
         FunSpecContextScope(
            this@FunSpecContextScope.description.appendContainer(testName),
            this@FunSpecContextScope.lifecycle,
            this,
            this@FunSpecContextScope.defaultConfig,
            this@FunSpecContextScope.coroutineContext,
         ).test()
      }
   }

   /**
    * Adds a container scope to this scope.
    */
   suspend fun xcontext(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      val testName = createTestName(name)
      addContainerTest(testName, xdisabled = true) {
         FunSpecContextScope(
            this@FunSpecContextScope.description.appendContainer(testName),
            this@FunSpecContextScope.lifecycle,
            this,
            this@FunSpecContextScope.defaultConfig,
            this@FunSpecContextScope.coroutineContext,
         ).test()
      }
   }

   fun context(name: String) =
      FunSpecContextConfigBuilder(
         createTestName(name),
         description,
         testContext,
         defaultConfig.toTestContainerConfig(),
         lifecycle,
         false
      )

   fun xcontext(name: String) =
      FunSpecContextConfigBuilder(
         createTestName(name),
         description,
         testContext,
         defaultConfig.toTestContainerConfig(),
         lifecycle,
         true
      )

   override suspend fun addTest(name: String, test: suspend TestContext.() -> Unit) {
      test(name, test)
   }

   /**
    * Adds a test scope to this scope, expecting config.
    */
   fun test(name: String) =
      TestWithConfigBuilder(createTestName(name), testContext, defaultConfig, xdisabled = false)

   /**
    * Adds a disabled test scope to this scope, expecting config.
    */
   fun xtest(name: String) =
      TestWithConfigBuilder(createTestName(name), testContext, defaultConfig, xdisabled = true)

   /**
    * Adds a test scope to this scope.
    */
   suspend fun test(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName(name), xdisabled = false, test = test)

   /**
    * Adds a disabled test scope to this scope.
    */
   suspend fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      addTest(createTestName(name), xdisabled = true, test = test)
}
