package io.kotest.core.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.DelayController

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
val TestContext.delayController: DelayController
   get() = when (val dispatcher = this.coroutineContext[CoroutineDispatcher.Key]) {
      is DelayController -> dispatcher
      else -> error("CoroutineDispatcher is not a delayController [$dispatcher]")
   }
