package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext

@ExperimentalKotest
internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate = DefaultSpecExecutorDelegate(defaultCoroutineDispatcherFactory, context)
