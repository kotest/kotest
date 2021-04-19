package io.kotest.core.spec.style.scopes

import io.kotest.core.spec.KotestDsl
import io.kotest.core.test.createTestName

/**
 * A scope that allows root tests to be registered using the syntax:
 *
 * feature("some context")
 * xfeature("some test")
 *
 */
@KotestDsl
interface FeatureSpecRootScope : RootScope {

   fun feature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) = addFeature(name, false, test)
   fun xfeature(name: String, test: suspend FeatureSpecContainerContext.() -> Unit) = addFeature(name, true, test)

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureSpecContainerContext.() -> Unit) {
      val testName = createTestName("Feature: ", name, false)
      registration().addContainerTest(testName, xdisabled = xdisabled) {
         FeatureSpecContainerContext(this).test()
      }
   }
}
