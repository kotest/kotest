package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestContext
import io.kotest.core.test.TestType
import io.kotest.core.test.createTestName

interface FunSpecRootScope : RootScope {

   /** Adds a top level [FunSpecContextScope] to this root scope. */
   fun context(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      val testName = createTestName(name)
      registration().addContainerTest(testName, xdisabled = false) {
         FunSpecContextScope(
            description().append(testName, TestType.Container),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   /** Adds a disabled top level [FunSpecContextScope] this root scope. */
   fun xcontext(name: String, test: suspend FunSpecContextScope.() -> Unit) {
      registration().addContainerTest(createTestName(name), xdisabled = true) {}
   }

   /** Adds a top level test case to this root scope. */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(createTestName(name), registration(), xdisabled = false)

   /** Adds a top level test, with the given name and test function, with default test config. */
   fun test(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName(name), xdisabled = false, test = test)

   /** Adds a disabled top level test case to this root scope. */
   fun xtest(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName(name), xdisabled = true, test = test)

   /** Adds a disabled top level test case with config to this root scope. */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(createTestName(name), registration(), xdisabled = true)

   /** Adds a top level test case to this root scope. */
   fun should(name: String) =
      RootTestWithConfigBuilder(createTestName("should ", name, true), registration(), xdisabled = false)

   /** Adds a top level test, with the given name and test function, with default test config. */
   fun should(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName("should ", name, true), xdisabled = false, test = test)

   /** Adds a disabled top level test case to this root scope. */
   fun xshould(name: String) =
      RootTestWithConfigBuilder(createTestName("should ", name, true), registration(), xdisabled = true)

   /** Adds a disabled top level test case with config to this root scope. */
   fun xshould(name: String, test: suspend TestContext.() -> Unit) =
      registration().addTest(createTestName("should ", name, true), xdisabled = true, test = test)

   /** Adds a top level test, with the given name and test function, with default test config. */
   fun expect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(createTestName("Expect: ", name, false), xdisabled = false, test = test)
   }

   /** Adds a top level test case to this root scope. */
   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(createTestName("Expect: ", name, false), registration(), xdisabled = false)
   }

   /** Adds a disabled top level test case to this root scope. */
   fun xexpect(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(createTestName("Expect: ", name, false), xdisabled = true, test = test)
   }

   /** Adds a disabled top level test case with config to this root scope. */
   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(createTestName("Expect: ", name, false), registration(), xdisabled = true)
   }
}
