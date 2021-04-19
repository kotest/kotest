package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

/**
 * Top level registration methods for ExpectSpec methods.
 */
@KotestDsl
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      registration().addContainerTest(testName, xdisabled = false) {
         ExpectSpecContainerContext(this).test()
      }
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerContext.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {
         ExpectSpecContainerContext(this).test()
      }
   }

   fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(createTestName("Expect: ", name, false), xdisabled = false, test = test)
   }

   fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(createTestName("Expect: ", name, false), xdisabled = true, test = test)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(createTestName("Expect: ", name, false), registration(), xdisabled = false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(createTestName("Expect: ", name, false), registration(), xdisabled = true)
   }
}
