package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 *
 * E.g.:
 *
 * ```
 * feature("some context") { }
 * xfeature("some test") { }
 * ```
 */
interface FeatureSpecRootScope : RootScope {

   fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = TestXMethod.NONE,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun ffeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = TestXMethod.FOCUSED,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = TestXMethod.DISABLED,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = TestXMethod.NONE,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun ffeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = TestXMethod.FOCUSED,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         xmethod = TestXMethod.DISABLED,
         context = this
      ) { FeatureSpecContainerScope(it) }
}
