package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext

@ExperimentalKotest
@Deprecated("Will be replaced by subsuming delegates into the spec executor directly")
internal actual fun createSpecExecutorDelegate(
   defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   context: EngineContext,
): SpecExecutorDelegate = DefaultSpecExecutorDelegate(defaultCoroutineDispatcherFactory, context)
