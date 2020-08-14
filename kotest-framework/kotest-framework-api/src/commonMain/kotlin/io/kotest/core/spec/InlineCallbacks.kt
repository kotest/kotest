package io.kotest.core.spec

import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult

/**
 * Defines functions which can be invoked with a lambda parameter to register callbacks.
 * This is an alternative style to using [FunctionCallbacks].
 */
interface InlineCallbacks {

   /**
    * Registers a callback to be executed before every [TestCase].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeTest(f: BeforeTest)

   /**
    * Registers a callback to be executed after every [TestCase].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterTest(f: AfterTest)

   /**
    * Registers a callback to be executed before every [TestCase]
    * with type [TestType.Container].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeContainer(f: BeforeContainer)

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Container].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterContainer(f: AfterContainer)

   /**
    * Registers a callback to be executed before every [TestCase]
    * with type [TestType.Test].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeEach(f: BeforeEach)

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterEach(f: AfterEach)

   /**
    * Registers a callback to be executed before every [TestCase]
    * with type [TestType.Test] or [TestType.Container].
    *
    * The [TestCase] about to be executed is provided as the parameter.
    */
   fun beforeAny(f: BeforeAny)

   /**
    * Registers a callback to be executed after every [TestCase]
    * with type [TestType.Container] or [TestType.Test].
    *
    * The callback provides two parameters - the test case that has just completed,
    * and the [TestResult] outcome of that test.
    */
   fun afterAny(f: AfterAny)
}
