package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.TestContext
import io.kotest.core.test.createTestName

@Deprecated("Renamed to DescribeSpecRootContext. This typealias will be removed in 4.8")
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
@KotestDsl
interface DescribeSpecRootContext : RootContext {

   fun context(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      test(testName, test)
   }

   @ExperimentalKotest
   fun context(name: String) =
      RootContextConfigBuilder(createTestName(name), registration(), false) { DescribeSpecContainerContext(it) }

   fun xcontext(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {}
   }

   @ExperimentalKotest
   fun xcontext(name: String) =
      RootContextConfigBuilder(createTestName(name), registration(), true) { DescribeSpecContainerContext(it) }

   fun describe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      test(testName, test)
   }

   @ExperimentalKotest
   fun describe(name: String) =
      RootContextConfigBuilder(createTestName("Describe: ", name, false), registration(), false) { DescribeSpecContainerContext(it) }

   fun xdescribe(name: String, test: suspend DescribeSpecContainerContext.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {}
   }

   @ExperimentalKotest
   fun xdescribe(name: String) =
      RootContextConfigBuilder(createTestName("Describe: ", name, false), registration(), true) { DescribeSpecContainerContext(it) }

   fun it(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(name = createTestName(name), xdisabled = false, test = test)
   }

   fun xit(name: String, test: suspend TestContext.() -> Unit) {
      registration().addTest(name = createTestName(name), xdisabled = true, test = test)
   }

   private fun test(testName: DescriptionName.TestName, test: suspend DescribeSpecContainerContext.() -> Unit) {
      registration().addContainerTest(testName, xdisabled = false) {
         DescribeSpecContainerContext(this).test()
      }
   }
}
