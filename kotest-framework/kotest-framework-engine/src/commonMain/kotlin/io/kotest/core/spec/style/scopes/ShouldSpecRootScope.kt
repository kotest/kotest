package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.TestDefinitionBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.test.TestScope
import io.kotest.core.test.TestType

/**
 * Allows tests to be registered in the 'ShouldSpec' fashion.
 *
 * ```
 * context("with context") {
 *   should("do something") {
 *     // test here
 *   }
 * }
 * ```
 *
 *  or
 *
 * ```
 * should("do something") {
 *   // test here
 * }
 * ```
 */
interface ShouldSpecRootScope : RootScope {

   /**
    * Adds a top-level context scope to the spec.
    */
   fun context(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .build { ShouldSpecContainerScope(this).test() }
      )
   }

   /**
    * Adds a top-level context scope to the spec.
    */
   fun fcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.FOCUSED)
            .build { ShouldSpecContainerScope(this).test() }
      )
   }

   /**
    * Adds a top-level context scope to the spec.
    */
   fun xcontext(name: String, test: suspend ShouldSpecContainerScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(contextName(name), TestType.Container)
            .withXmethod(TestXMethod.DISABLED)
            .build { ShouldSpecContainerScope(this).test() }
      )
   }

   /**
    * Adds a top-level context scope accepting config to the spec.
    */
   fun context(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.NONE,
         context = this
      ) { ShouldSpecContainerScope(it) }

   fun fcontext(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { ShouldSpecContainerScope(it) }

   /**
    * Adds a disabled top-level context scope accepting config to the spec.
    */
   fun xcontext(name: String): RootContainerWithConfigBuilder<ShouldSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = contextName(name),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { ShouldSpecContainerScope(it) }

   /**
    * Adds a top level test, with the given name and test function, with test config supplied
    * by invoking `.config` on the return of this function.
    */
   fun should(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = TestXMethod.NONE,
      )
   }

   /**
    * Adds a focused top level test, with the given name and test function, with test config supplied
    * by invoking `.config` on the return of this function.
    */
   fun fshould(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = TestXMethod.FOCUSED,
      )
   }

   /**
    * Adds a disabled top level test, with the given name and test function, with test config supplied
    * by invoking `.config` on the return of this function.
    */
   fun xshould(name: String): RootTestWithConfigBuilder {
      return RootTestWithConfigBuilder(
         context = this,
         name = shouldName(name),
         xmethod = TestXMethod.DISABLED,
      )
   }

   /**
    * Adds a top-level test, with the given name and test function, with default test config.
    */
   fun should(name: String, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(shouldName(name), TestType.Test)
            .build(test)
      )
   }

   /**
    * Adds a focused top-level test, with the given name and test function, with default test config.
    */
   fun fshould(name: String, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(shouldName(name), TestType.Test)
            .withXmethod(TestXMethod.FOCUSED)
            .build(test)
      )
   }

   /**
    * Adds a disabled top-level test, with the given name and test function, with default test config.
    */
   fun xshould(name: String, test: suspend TestScope.() -> Unit) {
      add(
         TestDefinitionBuilder
            .builder(shouldName(name), TestType.Test)
            .withXmethod(TestXMethod.DISABLED)
            .build(test)
      )
   }

   private fun contextName(name: String) =
      TestNameBuilder.builder(name).withPrefix("context ").withDefaultAffixes().build()

   private fun shouldName(name: String) =
      TestNameBuilder.builder(name).withPrefix("should ").withDefaultAffixes().build()

}
