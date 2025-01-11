package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.RootTest
import io.kotest.core.test.TestScope

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 */
interface FunSpecRootScope : RootScope {

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

   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xdisabled = false,
         context = this
      ) { FunSpecContainerScope(it) }

   @ExperimentalKotest
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
}
