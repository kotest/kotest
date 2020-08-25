package io.kotest.core.spec.style.scopes

import io.kotest.core.test.DescriptionName
import io.kotest.core.test.createTestName
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
      val testName = createTestName("Context: ", name, false)
      test(testName, test)
   }

   fun xcontext(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Context: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {}
   }

   fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      test(testName, test)
   }

   private fun test(testName: DescriptionName.TestName, test: suspend DescribeScope.() -> Unit) {
      registration().addContainerTest(testName, xdisabled = false) {
         DescribeScope(
            this@DescribeSpecRootScope.description().appendContainer(testName),
            this@DescribeSpecRootScope.lifecycle(),
            this,
            this@DescribeSpecRootScope.defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name, false)
      registration().addContainerTest(testName, xdisabled = true) {}
   }
}
