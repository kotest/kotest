package io.kotest.engine.spec

import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext

internal actual fun createSpecExecutorDelegate(
    defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
    context: EngineContext
): SpecExecutorDelegate = KotlinJsTestSpecExecutorDelegate(context)
