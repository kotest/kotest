package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.test.TestScope

/**
 * A context that allows tests to be registered using the syntax:
 *
 * ```
 * context("some test")
 * xcontext("some disabled test")
 * ```
 *
 * and
 *
 * ```
 * expect("some test")
 * expect("some test").config(...)
 * xexpect("some test")
 * xexpect("some test").config(...)
 * ```
 */
@KotestTestScope
class ExpectSpecContainerScope(
   val testScope: TestScope,
) : AbstractContainerScope(testScope) {

   suspend fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      context(name = name, xdisabled = false, test = test)
   }

   suspend fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      context(name = name, xdisabled = true, test = test)
   }

   private suspend fun context(
      name: String,
      xdisabled: Boolean,
      test: suspend ExpectSpecContainerScope.() -> Unit
   ) {
      registerContainer(
         name = TestName("Context: ", name, false),
         disabled = xdisabled,
         config = null
      ) { ExpectSpecContainerScope(this).test() }
   }

   suspend fun expect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xdisabled = false, test = test)
   }

   suspend fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xdisabled = true, test = test)
   }

   private suspend fun registerExpect(name: String, xdisabled: Boolean, test: suspend TestScope.() -> Unit) {
      registerTest(name = TestName("Expect: ", name, false), disabled = xdisabled, config = null, test = test)
   }

   suspend fun expect(name: String): TestWithConfigBuilder {
      TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName("Expect: ", name, false),
         context = this,
         xdisabled = false,
      )
   }

   suspend fun xexpect(name: String): TestWithConfigBuilder {
      TestDslState.startTest(name)
      return TestWithConfigBuilder(
         name = TestName("Expect: ", name, false),
         context = this,
         xdisabled = true,
      )
   }
}
