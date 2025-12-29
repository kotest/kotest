package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.RootTest
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 */
interface FunSpecRootScope : RootScope {

   /**
    * Adds a container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      context(name, TestXMethod.NONE, test)
   }

   /**
    * Adds a focused container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun fcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      context(name, TestXMethod.FOCUSED, test)

   /**
    * Adds a disabled container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      context(name, TestXMethod.DISABLED, test)

   fun context(name: String) = context(name, TestXMethod.NONE)
   fun fcontext(name: String) = context(name, TestXMethod.FOCUSED)
   fun xcontext(name: String) = context(name, TestXMethod.DISABLED)

   fun xcontext(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.DISABLED,
         context = this,
      ) { FunSpecContainerScope(it) }

   /**
    * Adds a [RootTest], with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder = test(name, TestXMethod.NONE)

   /**
    * Adds a focused [RootTest], with the given name and with config taken from the config builder.
    */
   fun ftest(name: String): RootTestWithConfigBuilder = test(name, TestXMethod.FOCUSED)

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder = test(name, TestXMethod.DISABLED)


   /**
    * Adds a focused [RootTest], with the given name and with config taken from the config builder.
    */
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      test(name, TestXMethod.FOCUSED, test)
   }

   /**
    * Adds a focused [RootTest], with the given name and default config.
    */
   fun ftest(name: String, test: suspend TestScope.() -> Unit) {
      test(name, TestXMethod.FOCUSED, test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and default config.
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

   private fun test(name: String, xMethod: TestXMethod, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = xMethod,
         config = null,
         test = test,
      )
   }

   private fun context(name: String, xmethod: TestXMethod, test: suspend FunSpecContainerScope.() -> Unit) =
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = xmethod,
         config = null
      ) { FunSpecContainerScope(this).test() }

   private fun context(name: String, xmethod: TestXMethod): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = xmethod,
         context = this,
      ) { FunSpecContainerScope(it) }
}
