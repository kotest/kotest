package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl

@Suppress("FunctionName")
interface WordSpecRootContext : RootContext {

   infix fun String.should(test: suspend WordSpecShouldContainerContext.() -> Unit) {
      val testName = TestName("$this should")
      registration().addContainerTest(testName, xdisabled = false) {
         WordSpecShouldContainerContext(this).test()
      }
   }

   infix fun String.xshould(test: suspend WordSpecShouldContainerContext.() -> Unit) {
      val testName = TestName("$this should")
      registration().addContainerTest(testName, xdisabled = true) {
         WordSpecShouldContainerContext(this).test()
      }
   }

   infix fun String.When(init: suspend WordSpecWhenContainerContext.() -> Unit) = addWhenContext(this, init)
   infix fun String.`when`(init: suspend WordSpecWhenContainerContext.() -> Unit) = addWhenContext(this, init)

   private fun addWhenContext(name: String, test: suspend WordSpecWhenContainerContext.() -> Unit) {
      val testName = TestName("$name when")
      registration().addContainerTest(testName, xdisabled = false) {
         WordSpecWhenContainerContext(this).test()
      }
   }
}
