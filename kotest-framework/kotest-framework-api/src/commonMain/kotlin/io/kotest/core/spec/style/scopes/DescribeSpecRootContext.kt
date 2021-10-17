package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.test.TestContext

@Deprecated("Renamed to DescribeSpecRootContext. Deprecated since 4.5.")
typealias DescribeSpecRootScope = DescribeSpecRootContext

/**
 * A context that allows root tests to be registered using the syntax:
 *
 * describe("some test")
 *
 * or
 *
 * xdescribe("some disabled test")
 */
interface DescribeSpecRootContext : RootContext {

   fun context(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = TestName("Context: ", name, false)
      test(testName, test)
   }

   @ExperimentalKotest
   fun context(name: String) =
      RootContextConfigBuilder(TestName(name), registration(), false) { DescribeSpecContainerContext(it) }

   fun xcontext(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = TestName("Context: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {}
   }

   @ExperimentalKotest
   fun xcontext(name: String) =
      RootContextConfigBuilder(TestName(name), registration(), true) { DescribeSpecContainerContext(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = TestName("Describe: ", name, null, false)
      test(testName, test)
   }

   @ExperimentalKotest
   fun describe(name: String) =
      RootContextConfigBuilder(
         TestName("Describe: ", name, false),
         registration(),
         false
      ) { DescribeSpecContainerContext(it) }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = TestName("Describe: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {}
   }

   @ExperimentalKotest
   fun xdescribe(name: String) =
      RootContextConfigBuilder(
         TestName("Describe: ", name, false),
         registration(),
         true
      ) { DescribeSpecContainerContext(it) }

   fun it(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(name = TestName(name), xdisabled = false, test = test)
   }

   fun xit(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(name = TestName(name), xdisabled = true, test = test)
   }

   private fun test(testName: TestName, test: suspend DescribeSpecContainerContext.() -> Unit) {
      registration().addContainerTest(testName, xdisabled = false) {
         val incomplete = IncompleteContainerContext(this)
         DescribeSpecContainerContext(incomplete).test()
         if (!incomplete.hasNestedTest) throw IncompleteContainerException(testName.testName)
      }
   }
}
