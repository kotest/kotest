package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestName
import kotlin.time.ExperimentalTime

/**
 * A context that allows root tests to be registered using the syntax:
 *
 * describe("some test")
 * xdescribe("some disabled test")
 */
@OptIn(ExperimentalTime::class)
interface DescribeSpecRootScope : RootScope {

   fun context(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Context: ", name)
      test(testName, test)
   }

   fun xcontext(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Context: ", name)
      registration().addContainerTest(testName, xdisabled = true) {}
   }

   fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Describe: ", name)
      test(testName, test)
   }

   private fun test(testName: TestName, test: suspend DescribeScope.() -> Unit) {
      registration().addContainerTest(testName, xdisabled = false) {
         DescribeScope(
            this@DescribeSpecRootScope.description().append(testName),
            this@DescribeSpecRootScope.lifecycle(),
            this,
            this@DescribeSpecRootScope.defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = TestName("Describe: ", name)
      registration().addContainerTest(testName, xdisabled = true) {}
   }
}
