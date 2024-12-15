package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestScope

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, false), false, null) { ExpectSpecContainerScope(this).test() }
   }

   /**
    * Adds a container test to this spec expecting config.
    */
   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Context: ", name, false), false, this) { ExpectSpecContainerScope(it) }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, false), true, null) { ExpectSpecContainerScope(this).test() }
   }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName("Expect: ", name, false), false, null) { ExpectSpecContainerScope(this).test() }
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName("Expect: ", name, false), true, null) { ExpectSpecContainerScope(this).test() }
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), true)
   }
}
