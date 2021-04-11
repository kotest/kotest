package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.createTestName

@Suppress("FunctionName")
@KotestDsl
interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldScope.() -> Unit) {
      val testName = createTestName("$this should")
      registration().addContainerTest(testName, xdisabled = false) {
         WordSpecShouldScope(this).test()
      }
   }

   infix fun String.xshould(test: suspend WordSpecShouldScope.() -> Unit) {
      val testName = createTestName("$this should")
      registration().addContainerTest(testName, xdisabled = true) {
         WordSpecShouldScope(this).test()
      }
   }

   infix fun String.When(init: suspend WordSpecWhenScope.() -> Unit) = addWhenContext(this, init)
   infix fun String.`when`(init: suspend WordSpecWhenScope.() -> Unit) = addWhenContext(this, init)

   private fun addWhenContext(name: String, test: suspend WordSpecWhenScope.() -> Unit) {
      val testName = createTestName("$name when")
      registration().addContainerTest(testName, xdisabled = false) {
         WordSpecWhenScope(this).test()
      }
   }
}
