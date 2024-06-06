package io.kotest.core.test

import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

val TestScope.testCoroutineScheduler: TestCoroutineScheduler
   get() = coroutineContext.testCoroutineScheduler

val CoroutineContext.testCoroutineScheduler: TestCoroutineScheduler
   get() = when (val dispatcher = this[ContinuationInterceptor]) {
      is TestDispatcher -> dispatcher.scheduler
      else -> error("Dispatcher is not a TestDispatcher: $dispatcher")
   }
