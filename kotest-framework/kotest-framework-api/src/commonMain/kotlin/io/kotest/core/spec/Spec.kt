package io.kotest.core.spec

import io.kotest.core.AutoClosing
import io.kotest.core.FunctionCallbacks
import io.kotest.core.InlineCallbacks
import io.kotest.core.InlineConfiguration
import io.kotest.core.SpecFunctionConfiguration
import io.kotest.core.TestConfiguration
import io.kotest.core.config.configuration
import io.kotest.core.js.JsTest
import io.kotest.core.test.TestCase

interface Spec : SpecFunctionConfiguration, AutoClosing, FunctionCallbacks, InlineCallbacks, InlineConfiguration {

   /**
    * Returns the tests defined in this spec as [RootTest] instances.
    *
    * If this spec does not create the tests cases upon instantiation, then this method
    * will materialize the tests (Eg when a test is defined as a function as in annotation spec).
    */
   fun materializeRootTests(): List<RootTest>

   /**
    * The annotation [JsTest] is intercepted by the kotlin.js compiler and invoked in the generated
    * javascript code. We need to hook into this function to invoke our test execution code which will
    * run tests defined by kotest.
    *
    * Kotest automatically installs a Javascript test-adapter to intercept calls to all tests so we can
    * avoid passing this special test-generating-test to the underyling javascript test framework so it
    * doesn't appear in test output / reports.
    */
   @JsTest
   fun javascriptTestInterceptor() {
      // TODO executeSpec(this)
   }
}

abstract class BaseSpec : TestConfiguration(), Spec {
   fun resolvedDefaultConfig() = defaultTestCaseConfig() ?: defaultTestConfig ?: configuration.defaultTestConfig
}

data class RootTest(val testCase: TestCase, val order: Int)
