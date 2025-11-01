package io.kotest.engine.coroutines

import kotlinx.coroutines.test.TestCoroutineScheduler

/**
 * Returns the kotlin.test [TestCoroutineScheduler] associated with this Kotest test.
 *
 * This element is available when coroutineTestScope is set to true.
 */
val io.kotest.core.test.TestScope.testScheduler: TestCoroutineScheduler
   get() = coroutineTestScope.testScheduler
