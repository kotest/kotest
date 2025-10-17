package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.test.TestScope

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, focused = false, disabled = false)
   }

   fun fcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, focused = true, disabled = false)
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, focused = false, disabled = true)
   }

   /**
    * Adds a container test to this spec expecting config.
    */
   fun context(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = false,
         xdisabled = false,
         context = this
      ) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   fun fcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = true,
         xdisabled = false,
         context = this
      ) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = false,
         xdisabled = true,
         context = this
      ) { ExpectSpecContainerScope(it) }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, focused = true, disabled = false)
   }

   fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, focused = true, disabled = false)
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, focused = false, disabled = true)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         focused = false,
         xdisabled = false
      )
   }

   fun fexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         focused = true,
         xdisabled = false
      )
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         focused = false,
         xdisabled = true
      )
   }

   private fun addContext(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      focused: Boolean,
      disabled: Boolean
   ) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Context: ").build(),
         focused = focused,
         disabled = disabled,
         config = null,
      ) { ExpectSpecContainerScope(this).test() }
   }

   private fun addExpect(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      focused: Boolean,
      disabled: Boolean
   ) {
      addTest(
         testName = TestNameBuilder.builder(name).withPrefix("Expect: ").build(),
         focused = focused,
         disabled = disabled,
         config = null,
      ) { ExpectSpecContainerScope(this).test() }
   }
}
