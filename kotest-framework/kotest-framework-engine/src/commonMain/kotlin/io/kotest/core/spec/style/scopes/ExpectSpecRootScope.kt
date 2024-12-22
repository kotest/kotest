package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestScope

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, disabled = false)
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, disabled = true)
   }

   /**
    * Adds a container test to this spec expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Context: ", name, false), false, this) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   @ExperimentalKotest
   fun xcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Context: ", name, true), false, this) { ExpectSpecContainerScope(it) }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, disabled = false)
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, disabled = true)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), true)
   }

   private fun addContext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit, disabled: Boolean) {
      addContainer(
         testName = TestName("Context: ", name, false),
         disabled = disabled,
         config = null
      ) { ExpectSpecContainerScope(this).test() }
   }

   private fun addExpect(name: String, test: suspend ExpectSpecContainerScope.() -> Unit, disabled: Boolean) {
      addTest(
         testName = TestName("Expect: ", name, false),
         disabled = disabled,
         config = null
      ) { ExpectSpecContainerScope(this).test() }
   }
}
