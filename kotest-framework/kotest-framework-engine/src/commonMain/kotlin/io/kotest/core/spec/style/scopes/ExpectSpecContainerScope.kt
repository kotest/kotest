package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
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
      context(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun fcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   suspend fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun context(
      name: String,
      xmethod: TestXMethod,
      test: suspend ExpectSpecContainerScope.() -> Unit
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = null
      ) { ExpectSpecContainerScope(this).test() }
   }

   suspend fun expect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   suspend fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   suspend fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun registerExpect(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      registerTest(name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(), xmethod = xmethod, config = null, test = test)
   }

   suspend fun expect(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("Expect: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.NONE,
      )
   }

   suspend fun fexpect(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("Expect: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.FOCUSED,
      )
   }

   suspend fun xexpect(name: String): TestWithConfigBuilder {
      val testName = TestNameBuilder.builder(name).withPrefix("Expect: ").build()
      TestDslState.startTest(testName)
      return TestWithConfigBuilder(
         name = testName,
         context = this,
         xmethod = TestXMethod.DISABLED,
      )
   }
}
