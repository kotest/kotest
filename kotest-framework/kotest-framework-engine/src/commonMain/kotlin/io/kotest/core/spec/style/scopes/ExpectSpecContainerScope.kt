package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.KotestTestScope
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

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

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun context(name: String, config: TestConfig, test: suspend ExpectSpecContainerScope.() -> Unit) {
      context(name = name, xmethod = TestXMethod.NONE, test = test, config = config)
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
      test: suspend ExpectSpecContainerScope.() -> Unit,
      config: TestConfig? = null
   ) {
      registerContainer(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = config
      ) { ExpectSpecContainerScope(this).test() }
   }

   suspend fun expect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.NONE, test = test)
   }

   /**
    * Adds a test with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   suspend fun expect(name: String, config: TestConfig, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.NONE, test = test, config = config)
   }

   suspend fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.FOCUSED, test = test)
   }

   suspend fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      registerExpect(name = name, xmethod = TestXMethod.DISABLED, test = test)
   }

   private suspend fun registerExpect(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit, config: TestConfig? = null) {
      registerTest(name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(), xmethod = xmethod, config = config, test = test)
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
