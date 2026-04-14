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
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.NONE)
            .build { ExpectSpecContainerScope(this).test() }
      )
   }

   fun fcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.FOCUSED)
            .build { ExpectSpecContainerScope(this).test() }
      )
   }

   fun xcontext(name: String, test: suspend ExpectSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.DISABLED)
            .build { ExpectSpecContainerScope(this).test() }
      )
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
      add(
         TestDefinitionBuilder
            .builder(expectName(name), TestType.Test)
            .withXmethod(TestXMethod.NONE)
            .build(test)
      )
   }

   fun fexpect(name: String, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(expectName(name), TestType.Test)
            .withXmethod(TestXMethod.FOCUSED)
            .build(test)
      )
   }

   fun xexpect(name: String, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(expectName(name), TestType.Test)
            .withXmethod(TestXMethod.DISABLED)
            .build(test)
      )
   }

   fun expect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = expectName(name),
         xmethod = TestXMethod.NONE,
      )
   }

   fun fexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = expectName(name),
         xmethod = TestXMethod.FOCUSED,
      )
   }

   fun xexpect(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = expectName(name),
         xmethod = TestXMethod.DISABLED,
      )
   }

   private fun expectName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Expect: ").build()
   private fun contextName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("Context: ").build()
}
