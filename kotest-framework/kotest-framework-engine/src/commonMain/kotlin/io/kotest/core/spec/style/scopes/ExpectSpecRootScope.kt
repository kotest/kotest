package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.NONE)
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

   fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.FOCUSED)
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.DISABLED)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         xmethod = TestXMethod.NONE
      )
   }

   fun fexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         xmethod = TestXMethod.FOCUSED
      )
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         xmethod = TestXMethod.DISABLED
      )
   }

   private fun addContext(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      xmethod: TestXMethod,
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         xmethod = xmethod,
         config = null,
      ) { ExpectSpecContainerScope(this).test() }
   }

   private fun addExpect(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      xmethod: TestXMethod,
   ) {
      addTest(
         testName = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         xmethod = xmethod,
         config = null,
      ) { ExpectSpecContainerScope(this).test() }
   }
}
