package io.kotest.core.listeners

import io.kotest.core.test.TestCase

@Suppress("DEPRECATION") // Remove when removing Listener
interface BeforeInvocationListener : Listener {

   /**
    * Invoked before each 'run' of a test, with a flag indicating the iteration number.
    * This callback is useful if you have set a test to have multiple invocations via config and want to do
    * some setup / teardown between runs.
    *
    * If you are running a test with the default single invocation then this callback is effectively the
    * same as [beforeTest][io.kotest.core.TestConfiguration.beforeTest].
    *
    * Note: If you have set multiple invocations _and_ multiple threads, then these callbacks could be
    * invoked concurrently.
    */
   suspend fun beforeInvocation(testCase: TestCase, iteration: Int): Unit = Unit
}

@Suppress("DEPRECATION") // Remove when removing Listener
interface AfterInvocationListener : Listener {

   /**
    * Invoked after each 'run' of a test, with a flag indicating the iteration number.
    * This callback is useful if you have set a test to have multiple invocations via config and want to do
    * some setup / teardown between runs.
    *
    * If you are running a test with the default single invocation then this callback is effectively the
    * same as [afterTest][io.kotest.core.TestConfiguration.afterTest].
    *
    * Note: If you have set multiple invocations _and_ multiple threads, then these callbacks could be
    * invoked concurrently.
    */
   suspend fun afterInvocation(testCase: TestCase, iteration: Int): Unit = Unit
}
