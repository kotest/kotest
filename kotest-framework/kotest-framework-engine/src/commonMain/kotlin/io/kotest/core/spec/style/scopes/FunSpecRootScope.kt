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
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.NONE,
         config = null
      ) { FunSpecContainerScope(this).test() }
   }

   fun fcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.FOCUSED,
         config = null
      ) { FunSpecContainerScope(this).test() }

   /**
    * Adds a disabled container [RootTest] that uses a [FunSpecContainerScope] as the test context.
    */
   fun xcontext(name: String, test: suspend FunSpecContainerScope.() -> Unit) =
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.DISABLED,
         config = null
      ) { FunSpecContainerScope(this).test() }

   fun context(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.NONE,
         context = this
      ) { FunSpecContainerScope(it) }

   fun fcontext(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { FunSpecContainerScope(it) }

   fun xcontext(name: String): RootContainerWithConfigBuilder<FunSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("context ").build(),
         xmethod = TestXMethod.DISABLED,
         context = this,
      ) { FunSpecContainerScope(it) }

   /**
    * Adds a [RootTest], with the given name and config taken from the config builder.
    */
   fun test(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.NONE,
      )

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun ftest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
      )

   /**
    * Adds a disabled [RootTest], with the given name and with config taken from the config builder.
    */
   fun xtest(name: String): RootTestWithConfigBuilder =
      RootTestWithConfigBuilder(
         context = this,
         name = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
      )

   /**
    * Adds a disabled [RootTest], with the given name and default config.
    */
   fun xtest(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.DISABLED,
         config = null,
         test = test,
      )
   }

   /**
    * Adds a [RootTest], with the given name and default config.
    */
   fun test(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.NONE,
         config = null,
         test = test,
      )
   }

   fun ftest(name: String, test: suspend TestScope.() -> Unit) {
      addTest(
         testName = TestNameBuilder.builder(name).build(),
         xmethod = TestXMethod.FOCUSED,
         config = null,
         test = test,
      )
   }
}
