package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestScope

@Deprecated("Renamed to ExpectSpecRootScope. Deprecated since 5.0")
typealias ExpectSpecRootContext = ExpectSpecRootScope

/**
 * Top level registration methods for ExpectSpec methods.
 */
@KotestDsl
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, null, false), false, null) { ExpectSpecContainerScope(this).test() }
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContainer(TestName("Context: ", name, null, false), true, null) { ExpectSpecContainerScope(this).test() }
   }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName("Expect: ", name, null, false), false, null) { ExpectSpecContainerScope(this).test() }
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addTest(TestName("Expect: ", name, null, false), true, null) { ExpectSpecContainerScope(this).test() }
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(this, TestName("Expect: ", name, null, false), true)
   }
}
