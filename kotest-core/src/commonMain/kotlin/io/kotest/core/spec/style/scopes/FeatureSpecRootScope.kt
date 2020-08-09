package io.kotest.core.spec.style.scopes

import io.kotest.core.test.TestName

/**
 * A scope that allows root tests to be registered using the syntax:
 *
 * feature("some context")
 * xfeature("some test")
 *
 */
interface FeatureSpecRootScope : RootScope {

   fun feature(name: String, test: suspend FeatureScope.() -> Unit) = addFeature(name, false, test)
   fun xfeature(name: String, test: suspend FeatureScope.() -> Unit) = addFeature(name, true, test)

   fun addFeature(name: String, xdisabled: Boolean, test: suspend FeatureScope.() -> Unit) {
      val testName = TestName("Feature: ", name)
      registration().addContainerTest(testName, xdisabled = xdisabled) {
         FeatureScope(
            description().append(testName),
            lifecycle(),
            this,
            defaultConfig(),
            this.coroutineContext,
         ).test()
      }
   }
}
