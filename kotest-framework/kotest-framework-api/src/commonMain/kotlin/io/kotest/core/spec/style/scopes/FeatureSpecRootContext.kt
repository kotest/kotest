package io.kotest.core.spec.style.scopes

import io.kotest.core.names.TestName
import io.kotest.core.spec.KotestDsl

/**
 * A scope that allows root tests to be registered using the syntax:
 *
 * feature("some context")
 * xfeature("some test")
 *
 */
@KotestDsl
interface FeatureSpecRootContext : RootContext {

   fun feature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) = addFeature(name, false, test)
   fun xfeature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) = addFeature(name, true, test)

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureSpecContainerContext.() -> Unit) {
      val testName = TestName("Feature: ", name, false)
      registration().addContainerTest(testName, xdisabled = xdisabled) {
         FeatureSpecContainerContext(this).test()
      }
   }
}
