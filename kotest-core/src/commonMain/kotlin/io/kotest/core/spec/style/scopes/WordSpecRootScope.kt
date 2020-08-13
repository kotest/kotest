package io.kotest.core.spec.style.scopes

import io.kotest.core.test.DescriptionType
import io.kotest.core.test.TestName

@Suppress("FunctionName")
interface WordSpecRootScope : RootScope {

   infix fun String.should(test: suspend WordSpecShouldScope.() -> Unit) {
      val testName = TestName("$this should")
      registration().addContainerTest(testName, xdisabled = false) {
         WordSpecShouldScope(
            description().append(testName, DescriptionType.Container),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   infix fun String.xshould(test: suspend WordSpecShouldScope.() -> Unit) {
      val testName = TestName("$this should")
      registration().addContainerTest(testName, xdisabled = true) {
         WordSpecShouldScope(
            description().append(testName, DescriptionType.Container),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }

   infix fun String.When(init: suspend WordSpecWhenScope.() -> Unit) = addWhenContext(this, init)
   infix fun String.`when`(init: suspend WordSpecWhenScope.() -> Unit) = addWhenContext(this, init)

   private fun addWhenContext(name: String, init: suspend WordSpecWhenScope.() -> Unit) {
      val testName = TestName("$name when")
      registration().addContainerTest(testName, xdisabled = false) {
         WordSpecWhenScope(
            description().append(testName, DescriptionType.Container),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).init()
      }
   }
}
