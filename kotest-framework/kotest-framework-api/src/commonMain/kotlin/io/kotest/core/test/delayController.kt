package io.kotest.core.test

import io.kotest.common.testCoroutineSchedulerOrNull
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

val TestScope.testCoroutineScheduler: TestCoroutineScheduler
   get() = coroutineContext.testCoroutineScheduler

val CoroutineContext.testCoroutineScheduler: TestCoroutineScheduler
   get() = testCoroutineSchedulerOrNull ?: error("Dispatcher is not a TestDispatcher: ${this[ContinuationInterceptor]}")
