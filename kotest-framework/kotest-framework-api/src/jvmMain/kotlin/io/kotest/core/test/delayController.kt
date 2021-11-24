package io.kotest.core.test

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.DelayController
import kotlin.coroutines.CoroutineContext

@ExperimentalStdlibApi
@ExperimentalCoroutinesApi
val TestScope.delayController: DelayController
   get() = coroutineContext.delayController

@ExperimentalCoroutinesApi
@OptIn(ExperimentalStdlibApi::class)
val CoroutineContext.delayController: DelayController
   get() = when (val dispatcher = this[CoroutineDispatcher.Key]) {
      is DelayController -> dispatcher
      else -> error("CoroutineDispatcher is not a delayController [$dispatcher]")
   }
