package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 */
interface FunSpecRootScope : RootScope {

   /**
    * Adds a container that uses a [FunSpecContainerScope] as the test context.
    */
   fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      context(name, TestXMethod.NONE, test)
   }

   /**
    * Adds a focused container that uses a [FunSpecContainerScope] as the test context.
    */
   fun fcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      context(name, TestXMethod.FOCUSED, test)

   /**
    * Adds a disabled container that uses a [FunSpecContainerScope] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      context(name, TestXMethod.DISABLED, test)

   fun context(name: String) = context(name, TestXMethod.NONE)
   fun fcontext(name: String) = context(name, TestXMethod.FOCUSED)
   fun xcontext(name: String) = context(name, TestXMethod.DISABLED)

   /**
    * Adds a test with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder = test(name, TestXMethod.NONE)

   /**
    * Adds a focused test, with the given name and with config taken from the config builder.
    */
   fun ftest(name: String): RootTestWithConfigBuilder = test(name, TestXMethod.FOCUSED)

   /**
    * Adds a disabled test, with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder = test(name, TestXMethod.DISABLED)

   /**
    * Adds a test, with the given name and with config taken from the config builder.
    */
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      test(name, TestXMethod.NONE, test)
   }

   /**
    * Adds a focused test, with the given name and default config.
    */
   fun ftest(name: String, test: suspend TestScope.() -> Unit) {
      test(name, TestXMethod.FOCUSED, test)
   }

   /**
    * Adds a disabled test, with the given name and default config.
    */
   fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      test(name, TestXMethod.DISABLED, test)
   }

   private fun test(name: String, xMethod: TestXMethod): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).build(),
         xmethod = xMethod,
      )
   }

   private fun test(name: String, xmethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder.builder(
            TestNameBuilder.builder(name).build(),
            TestType.Test,
         ).withXmethod(xmethod)
            .build { FunSpecContainerScope(this).test() }
      )
   }

   private fun context(name: String, xmethod: TestXMethod, test: suspend FunSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder.builder(contextName(name), TestType.Container)
            .withXmethod(xmethod)
            .build { FunSpecContainerScope(this).test() }
      )
   }

   private fun context(name: String, xmethod: TestXMethod): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = xmethod,
         context = this,
      ) { FunSpecContainerScope(it) }

   fun contextName(name: String): TestName = TestNameBuilder.builder(name).withPrefix("context ").build()
}
