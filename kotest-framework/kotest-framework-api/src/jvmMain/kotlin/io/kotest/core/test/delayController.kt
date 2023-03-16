package io.kotest.core.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlin.coroutines.CoroutineContext

@Deprecated("this will fail")
@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
val TestScope.testCoroutineScheduler: TestCoroutineScheduler
   get() = coroutineContext.testCoroutineScheduler

@Deprecated("this will fail")
@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
val CoroutineContext.testCoroutineScheduler: TestCoroutineScheduler
   get() = when (val dispatcher = this[CoroutineDispatcher]) {
      is TestDispatcher -> dispatcher.scheduler
      else -> error("CoroutineDispatcher is not a TestDispatcher [$dispatcher]")
   }
