package io.kotest.core.config

import io.kotest.core.test.TestNameCase

interface Configuration {

   /**
    * Returns the parallelization factor for executing specs.
    * If larger than 1, then k specs will be executed in parallel.
    * Any specs that should be executed in isolation should be marked with @DoNotParallelize.
    *
    * Defaults to 1, ie all specs executed in isolation.
    */
   fun parallelism(): Int

   /**
    * Returns the timeout for the execution of a test case.
    * Note: This timeout includes the time required to executed nested tests.
    *
    * Defaults to [Defaults.defaultTimeoutInMillis].
    */
   fun timeout(): Long

   /**
    * Returns the timeout for any single invocation of a test.
    *
    * Defaults to [Defaults.defaultInvocationTimeoutInMillis].
    */
   fun invocationTimeout(): Long

   /**
    * If this method returns true, then test names include prefixes like Given: and Context:
    *
    * Defaults to false.
    */
   fun includeTestScopePrefixes(): Boolean

   /**
    * Controls the case formatting of test names.
    *
    * Defaults to no change, [TestNameCase.AsIs].
    */
   fun testNameCase(): TestNameCase

   /**
    * If true, then the test execution will fail if any test is set to ignored.
    *
    * Defaults to false.
    */
   fun failOnIgnoredTests(): Boolean

   /**
    * If true, then all tests are implicitly wrapped in an assertSoftly call.
    *
    * Defaults to false.
    */
   fun globalAssertSoftly(): Boolean
}
