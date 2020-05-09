package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecScope : RootScope {

   fun context(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = createTestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = false) {
         ExpectScope(description().append(testName), lifecycle(), this, defaultConfig()).test()
      }
   }

   fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = createTestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = true) {
         ExpectScope(description().append(testName), lifecycle(), this, defaultConfig()).test()
      }
   }

   fun expect(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName("Expect: ", name)
      registration().addTest(testName, xdisabled = false, test = test)
   }

   fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      val testName = createTestName("Expect: ", name)
      registration().addTest(testName, xdisabled = true, test = test)
   }

   fun expect(name: String) =
      RootTestWithConfigBuilder(createTestName("Expect: ", name), registration(), xdisabled = false)

   fun xexpect(name: String) =
      RootTestWithConfigBuilder(createTestName("Expect: ", name), registration(), xdisabled = true)
}
