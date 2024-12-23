package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName

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
         testName = TestName("Feature: ", name, false),
         disabled = false,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) {
      addContainer(
         testName = TestName("Feature: ", name, false),
         disabled = true,
         config = null
      ) { FeatureSpecContainerScope(this).test() }
   }

   @ExperimentalKotest
   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Feature: ", name, false), false, this) { FeatureSpecContainerScope(it) }

   @ExperimentalKotest
   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Feature: ", name, false), true, this) { FeatureSpecContainerScope(it) }
}
