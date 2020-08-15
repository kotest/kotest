package io.kotest.core.spec.style.scopes

import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestContext

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = DescriptionName.TestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = false) {
         ExpectScope(
            description().appendContainer(testName),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   fun xcontext(name: String, test: suspend ExpectScope.() -> Unit) {
      val testName = DescriptionName.TestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = true) {
         ExpectScope(
            description().appendContainer(testName),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(DescriptionName.TestName("Expect: ", name), xdisabled = false, test = test)
   }

   fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(DescriptionName.TestName("Expect: ", name), xdisabled = true, test = test)
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(DescriptionName.TestName("Expect: ", name), registration(), xdisabled = false)
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(DescriptionName.TestName("Expect: ", name), registration(), xdisabled = true)
   }
}
