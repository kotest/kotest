package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName
import io.kotest.core.spec.RootTest
import io.kotest.core.test.TestContext

@Deprecated("Renamed to FunSpecRootContext. Deprecated since 4.5.")
typealias FunSpecRootScope = FunSpecRootContext

/**
 * Extends [RootContext] with dsl-methods for the 'fun spec' style.
 */
interface FunSpecRootContext : RootContext {

   /**
    * Adds a container [RootTest] that uses a [FunSpecContainerContext] as the test context.
    */
   fun context(name: String, test: suspend FunSpecContainerContext.() -> Unit) {
      addContainer(TestName("context ", name, false), false, null) { FunSpecContainerContext(this).test() }
   }

   /**
    * Adds a disabled container [RootTest] that uses a [FunSpecContainerContext] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerContext.() -> Unit) =
      addContainer(TestName("context ", name, false), true, null) { FunSpecContainerContext(this).test() }

   @ExperimentalKotest
   fun context(name: String) {
      RootContextConfigBuilder(TestName("context ", name, false), false, this) { FunSpecContainerContext(it) }
   }

   @ExperimentalKotest
   fun xcontext(name: String) {
      RootContextConfigBuilder(TestName("context ", name, false), true, this) { FunSpecContainerContext(it) }
   }

   /**
    * Adds a [RootTest], with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName(name), xdisabled = false)

   /**
    * Adds a [RootTest], with the given name and default config.
    */
   fun test(name: String, test: suspend TestContext.() -> Unit) = addTest(TestName(name), false, null, test)

   /**
    * Adds a disabled [RootTest], with the given name and default config.
    */
   fun xtest(name: String, test: suspend TestContext.() -> Unit) = addTest(TestName(name), true, null, test)

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(this, TestName(name), xdisabled = true)
}
