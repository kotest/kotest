package io.kotest.core.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.CoroutineContext

@ExperimentalStdlibApi
val TestScope.testCoroutineScheduler: TestCoroutineScheduler
   get() = coroutineContext.testCoroutineScheduler

@ExperimentalStdlibApi
val CoroutineContext.testCoroutineScheduler: TestCoroutineScheduler
   get() = when (val dispatcher = this[CoroutineDispatcher]) {
      is TestDispatcher -> dispatcher.scheduler
      else -> error("CoroutineDispatcher is not a TestDispatcher [$dispatcher]")
   }
