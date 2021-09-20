package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestContext

@Deprecated("Renamed to ExpectSpecRootContext. This typealias will be removed in 4.8")
typealias ExpectSpecRootScope = ExpectSpecRootContext

/**
 * Top level registration methods for ExpectSpec methods.
 */
@KotestDsl
interface ExpectSpecRootContext : RootContext {

   fun context(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      val testName = TestName("Context: ", name, null, false)
      registration().addContainerTest(testName, xdisabled = false) {
         ExpectSpecContainerContext(this).test()
      }
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      val testName = TestName("Context: ", name, null, false)
      registration().addContainerTest(testName, xdisabled = true) {
         ExpectSpecContainerContext(this).test()
      }
   }

   fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(TestName("Expect: ", name, null, false), xdisabled = false, test = test)
   }

   fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(TestName("Expect: ", name, null, false), xdisabled = true, test = test)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(TestName("Expect: ", name, null, false), registration(), xdisabled = false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(TestName("Expect: ", name, null, false), registration(), xdisabled = true)
   }
}
