package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.style.KotestDsl
import io.kotest.core.test.Description
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName
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
   override val coroutineContext: CoroutineContext
) : ContainerScope {

   /**
    * Adds a nested context scope to this scope.
    */
   suspend fun context(name: String, test: suspend ShouldSpecContextScope.() -> Unit) {
      addContainerTest(TestName(name), xdisabled = false) {
         ShouldSpecContextScope(
            this@ShouldSpecContextScope.description.append(name),
            this@ShouldSpecContextScope.lifecycle,
            this,
            this@ShouldSpecContextScope.defaultConfig,
            this@ShouldSpecContextScope.coroutineContext
         ).test()
      }
   }

   fun should(name: String) =
      TestWithConfigBuilder(TestName("should ", name), testContext, defaultConfig, xdisabled = false)

   fun xshould(name: String) =
      TestWithConfigBuilder(TestName("should ", name), testContext, defaultConfig, xdisabled = true)

   suspend fun should(name: String, test: suspend TestContext.() -> Unit) =
      addTest(TestName("should ", name), xdisabled = false, test = test)

   suspend fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      addTest(TestName("should ", name), xdisabled = true, test = test)
}
