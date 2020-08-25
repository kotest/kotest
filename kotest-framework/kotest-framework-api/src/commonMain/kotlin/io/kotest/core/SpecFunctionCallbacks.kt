package io.kotest.core

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Defines functions which can be overriden to register callbacks.
 * This is an alternative style to using [InlineCallbacks].
 */
interface SpecFunctionCallbacks {

   fun beforeSpec(spec: Spec) {}

   fun afterSpec(spec: Spec) {}

   /**
    * This function is invoked before every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeTest(testCase: TestCase) {}

   /**
    * This function is invoked after every [TestCase] in this Spec.
    * Override this function to provide custom behavior.
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun afterTest(testCase: TestCase, result: TestResult) {}

   fun beforeContainer(testCase: TestCase) {}

   fun afterContainer(testCase: TestCase, result: TestResult) {}

   fun beforeEach(testCase: TestCase) {}

   fun afterEach(testCase: TestCase, result: TestResult) {}

   fun beforeAny(testCase: TestCase) {}

   fun afterAny(testCase: TestCase, result: TestResult) {}
}
