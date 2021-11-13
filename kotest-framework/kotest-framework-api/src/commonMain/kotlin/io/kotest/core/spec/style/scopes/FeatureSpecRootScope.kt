package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

@Deprecated("Renamed to FeatureSpecRootContext. Deprecated since 5.0")
typealias FeatureSpecRootContext = FeatureSpecRootScope

/**
 * Extends [RootScope] with dsl-methods for the 'fun spec' style.
 *
 * Eg:
 *   feature("some context") { }
 *   xfeature("some test") { }
 *
 */
interface FeatureSpecRootScope : RootScope {

   fun feature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) = addFeature(name, false, test)
   fun xfeature(name: String, test: suspend FeatureSpecContainerScope.() -> Unit) = addFeature(name, true, test)

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureSpecContainerScope.() -> Unit) {
      val testName = TestName("Feature: ", name, false)
      addContainer(testName, xdisabled, null) { FeatureSpecContainerScope(this).test() }
   }
}
