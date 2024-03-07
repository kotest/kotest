package io.kotest.core.spec.style.scopes

import io.kotest.common.ExperimentalKotest
import io.kotest.core.names.TestName

@Deprecated("Renamed to FeatureSpecRootContext. Deprecated since 5.0")
typealias FeatureSpecRootContext = FeatureSpecRootScope

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

   fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) =
      addFeature(name = name, xdisabled = false, test = test)

   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) =
      addFeature(name = name, xdisabled = true, test = test)

   @ExperimentalKotest
   fun feature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Feature: ", name, false), false, this) { FeatureSpecContainerScope(it) }

   @ExperimentalKotest
   fun xfeature(name: String): RootContainerWithConfigBuilder<FeatureSpecContainerScope> =
      RootContainerWithConfigBuilder(TestName("Feature: ", name, false), true, this) { FeatureSpecContainerScope(it) }

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureSpecContainerScope.() -> Unit) {
      val testName = TestName("Feature: ", name, false)
      addContainer(testName, xdisabled, null) { FeatureSpecContainerScope(this).test() }
   }
}
