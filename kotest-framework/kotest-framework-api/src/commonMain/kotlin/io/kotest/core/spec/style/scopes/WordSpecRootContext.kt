package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

@Deprecated("Renamed to WordSpecRootContext. Deprecated since 4.5.")
typealias WordSpecRootScope = WordSpecRootContext

@Suppress("FunctionName")
interface WordSpecRootContext : RootContext {

   infix fun String.should(test: suspend WordSpecShouldContainerContext.() -> Unit) {
      val testName = TestName("$this should")
      registration().addContainerTest(testName, xdisabled = false) {
         val incomplete = IncompleteContainerContext(this)
         WordSpecShouldContainerContext(incomplete).test()
         if (!incomplete.hasNestedTest) throw IncompleteContainerException(testName.testName)
      }
   }

   infix fun String.xshould(test: suspend WordSpecShouldContainerContext.() -> Unit) {
      val testName = TestName("$this should")
      registration().addContainerTest(testName, xdisabled = true) {
         val incomplete = IncompleteContainerContext(this)
         WordSpecShouldContainerContext(incomplete).test()
         if (!incomplete.hasNestedTest) throw IncompleteContainerException(testName.testName)
      }
   }

   infix fun String.When(init: suspend WordSpecWhenContainerContext.() -> Unit) = addWhenContext(this, init)
   infix fun String.`when`(init: suspend WordSpecWhenContainerContext.() -> Unit) = addWhenContext(this, init)

   private fun addWhenContext(name: String, test: suspend WordSpecWhenContainerContext.() -> Unit) {
      val testName = TestName("$name when")
      registration().addContainerTest(testName, xdisabled = false) {
         val incomplete = IncompleteContainerContext(this)
         WordSpecWhenContainerContext(incomplete).test()
         if (!incomplete.hasNestedTest) throw IncompleteContainerException(testName.testName)
      }
   }
}
