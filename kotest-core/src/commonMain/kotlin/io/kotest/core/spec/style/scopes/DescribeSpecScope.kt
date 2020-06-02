package io.kotest.core.spec.style.scopes

import io.kotest.core.test.createTestName
import kotlin.time.ExperimentalTime

/**
 * A context that allows tests to be registered using the syntax:
 *
 * describe("some test")
 * xdescribe("some disabled test")
 */
@OptIn(ExperimentalTime::class)
interface DescribeSpecScope : RootScope {

   fun describe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name)
      registration().addContainerTest(testName, xdisabled = false) {
         DescribeScope(description().append(testName), lifecycle(), this, defaultConfig()).test()
      }
   }

   fun xdescribe(name: String, test: suspend DescribeScope.() -> Unit) {
      val testName = createTestName("Describe: ", name)
      registration().addContainerTest(testName, xdisabled = true) {}
   }
}
