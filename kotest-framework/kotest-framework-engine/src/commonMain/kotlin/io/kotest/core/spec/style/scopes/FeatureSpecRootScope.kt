package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestNameBuilder

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
         focused = false,
         disabled = false,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun ffeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         focused = true,
         disabled = false,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         focused = false,
         disabled = true,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         focused = false,
         xdisabled = false,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun ffeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         focused = true,
         xdisabled = false,
         context = this
      ) { FeatureSpecContainerScope(it) }

   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(
         name = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         focused = false,
         xdisabled = true,
         context = this
      ) { FeatureSpecContainerScope(it) }
}
