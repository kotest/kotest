package io.kotest.engine.spec

import io.kotest.engine.interceptors.EngineContext

@Suppress("DEPRECATION")
internal actual fun createSpecExecutorDelegate(
    context: EngineContext
): SpecExecutorDelegate = KotlinJsTestSpecExecutorDelegate(context)
