package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestContext
import io.kotest.core.test.TestName

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecScope : RootScope {

   fun context(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = TestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = false) {
         ExpectScope(description().append(testName), lifecycle(), this, defaultConfig(), this.coroutineContext).test()
      }
   }

   fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = TestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = true) {
         ExpectScope(description().append(testName), lifecycle(), this, defaultConfig(), this.coroutineContext).test()
      }
   }

   fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(TestName("Expect: ", name), xdisabled = false, test = test)
   }

   fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(TestName("Expect: ", name), xdisabled = true, test = test)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(TestName("Expect: ", name), registration(), xdisabled = false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(TestName("Expect: ", name), registration(), xdisabled = true)
   }
}
