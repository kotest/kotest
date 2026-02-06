package io.kotest.core.spec.style.scopes

import io.kotest.common.KotestInternal
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.config.TestConfig

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.NONE)
   }

   /**
    * Adds a test case with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun context(name: String, config: TestConfig, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.NONE, config = config)
   }

   fun fcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.FOCUSED)
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.DISABLED)
   }

   /**
    * Adds a container test to this spec expecting config.
    */
   fun context(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = TestXMethod.NONE,
         context = this
      ) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   fun fcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { ExpectSpecContainerScope(it) }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.NONE)
   }

   /**
    * Adds a test with config passed as a param.
    * Marked as internal as it should be used only by the data test registrars.
    */
   @KotestInternal
   fun expect(name: String, config: TestConfig, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.NONE, config = config)
   }

   fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.FOCUSED)
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.DISABLED)
   }

   fun expect(name: String): RootTestWithConfigBuilder = addExpect(name, TestXMethod.NONE)
   fun fexpect(name: String): RootTestWithConfigBuilder = addExpect(name, TestXMethod.FOCUSED)
   fun xexpect(name: String): RootTestWithConfigBuilder = addExpect(name, TestXMethod.DISABLED)

   private fun addContext(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      xmethod: TestXMethod,
      config: TestConfig? = null,
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = config,
      ) { ExpectSpecContainerScope(this).test() }
   }

   private fun addExpect(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      xmethod: TestXMethod,
      config: TestConfig? = null,
   ) {
      addTest(
         testName = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         xmethod = xmethod,
         config = config,
      ) { ExpectSpecContainerScope(this).test() }
   }

   private fun addExpect(
      name: String,
      xmethod: TestXMethod,
   ): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         xmethod = xmethod,
      )
   }
}
