package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
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
         disabled = false,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestNameBuilder.builder(name).withPrefix("Feature: ").build(),
         disabled = true,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestNameBuilder.builder(name).withPrefix("Feature: ").build(), false, this) { FeatureSpecContainerScope(it) }

   @ExperimentalKotest
   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestNameBuilder.builder(name).withPrefix("Feature: ").build(), true, this) { FeatureSpecContainerScope(it) }
}
