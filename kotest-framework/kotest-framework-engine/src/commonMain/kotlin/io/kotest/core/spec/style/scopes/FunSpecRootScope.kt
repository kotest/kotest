package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.RootTest
import io.kotest.core.test.TestScope
import io.kotest.datatest.WithDataRootRegistrar

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 */
interface FunSpecRootScope : RootScope, WithDataRootRegistrar<FunSpecContainerScope> {

   /**
    * Adds a container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun context(name: String, test: suspend FunSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("context ").build(),
         disabled = false,
         config = null
      ) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("context ").build(),
         disabled = true,
         config = null
      ) { FunSpecContainerScope(this).test() }

   fun context(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xdisabled = false,
         context = this
      ) { FunSpecContainerScope(it) }

   fun xcontext(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xdisabled = true,
         context = this
      ) { FunSpecContainerScope(it) }

   /**
    * Adds a [RootTest], with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(context = this, name = TestNameBuilder.builder(name).build(), xdisabled = false)

   /**
    * Adds a [RootTest], with the given name and default config.
    */
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestNameBuilder.builder(name).build(), disabled = false, config = null, test = test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and default config.
    */
   fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestNameBuilder.builder(name).build(), disabled = true, config = null, test = test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(context = this, name = TestNameBuilder.builder(name).build(), xdisabled = true)

   override fun registerWithDataTest(
      name: String,
      test: suspend FunSpecContainerScope.() -> Unit
   ) {
      context(name) { test() }
   }
}
