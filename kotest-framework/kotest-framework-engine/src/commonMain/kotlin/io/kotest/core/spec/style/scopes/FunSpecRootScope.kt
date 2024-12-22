package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
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
         testName = TestName("context ", name, false),
         disabled = false,
         config = null
      ) { FunSpecContainerScope(this).test() }
   }

   /**
    * Adds a disabled container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      addContainer(
         testName = TestName("context ", name, false),
         disabled = true,
         config = null
      ) { FunSpecContainerScope(this).test() }

   @ExperimentalKotest
   fun context(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("context ", name, false), false, this) { FunSpecContainerScope(it) }

   @ExperimentalKotest
   fun xcontext(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("context ", name, false), true, this) { FunSpecContainerScope(it) }

   /**
    * Adds a [RootTest], with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName(name), xdisabled = false)

   /**
    * Adds a [RootTest], with the given name and default config.
    */
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestName(name), disabled = false, config = null, test = test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and default config.
    */
   fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      addTest(testName = TestName(name), disabled = true, config = null, test = test)
   }

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName(name), xdisabled = true)
}
