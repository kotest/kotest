package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.kotlinJsTestFrameworkAvailable

@ExperimentalKotest
internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate =
   if (kotlinJsTestFrameworkAvailable()) {
      DefaultSpecExecutorDelegate(defaultCoroutineDispatcherFactory, context)
   } else {
      DefaultSpecExecutorDelegate(defaultCoroutineDispatcherFactory, context)
   }
