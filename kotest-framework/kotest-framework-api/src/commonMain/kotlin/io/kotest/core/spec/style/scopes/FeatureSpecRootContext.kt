package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName

@Deprecated("Renamed to FeatureSpecRootContext. Deprecated since 4.5.")
typealias FeatureSpecRootScope = FeatureSpecRootContext

/**
 * Extends [RootContext] with dsl-methods for the 'fun spec' style.
 *
 * Eg:
 *   feature("some context") { }
 *   xfeature("some test") { }
 *
 */
interface FeatureSpecRootContext : RootContext {

   fun feature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) = addFeature(name, false, test)
   fun xfeature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) = addFeature(name, true, test)

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureSpecContainerContext.() -> Unit) {
      val testName = TestName("Feature: ", name, false)
      addContainer(testName, xdisabled, null) { FeatureSpecContainerContext(this).test() }
   }
}
