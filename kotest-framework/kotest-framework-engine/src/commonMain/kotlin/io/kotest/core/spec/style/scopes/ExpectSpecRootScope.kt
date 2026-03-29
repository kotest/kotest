package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

/**
 * Top level registration methods for ExpectSpec methods.
 */
interface ExpectSpecRootScope : RootScope {

   fun context(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.NONE)
   }

   fun fcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.FOCUSED)
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      addContext(name = name, test = test, xmethod = TestXMethod.DISABLED)
   }

   /**
    * Adds a container test to this spec expecting config.
    */
   fun context(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.NONE,
         context = this
      ) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   fun fcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { ExpectSpecContainerScope(it) }

   /**
    * Adds a disabled container test to this spec expecting config.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ExpectSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { ExpectSpecContainerScope(it) }

   fun expect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.NONE)
   }

   fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.FOCUSED)
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      addExpect(name = name, test = test, xmethod = TestXMethod.DISABLED)
   }

   fun expect(name: String): RootTestWithConfigBuilder = addExpect(name, TestXMethod.NONE)
   fun fexpect(name: String): RootTestWithConfigBuilder = addExpect(name, TestXMethod.FOCUSED)
   fun xexpect(name: String): RootTestWithConfigBuilder = addExpect(name, TestXMethod.DISABLED)

   private fun addContext(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      xmethod: TestXMethod,
   ) {
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { ExpectSpecContainerScope(this).test() }
      )
   }

   private fun addExpect(
      name: String,
      test: suspend ExpectSpecContainerScope.() -> Unit,
      xmethod: TestXMethod,
   ) {
      add(
         TestDefinitionBuilder
            .builder(expectName(name), TestType.Test)
            .withXmethod(xmethod)
            .build { ExpectSpecContainerScope(this).test() }
      )
   }

   private fun addExpect(
      name: String,
      xmethod: TestXMethod,
   ): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = expectName(name),
         xmethod = xmethod,
      )
   }

   private fun expectName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Expect: ").build()

   private fun contextName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Context: ").build()
}
