package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.interceptors.EngineContext

@Suppress("DEPRECATION")
@ExperimentalKotest
@Deprecated("Will be replaced by subsuming delegates into the spec executor directly")
internal actual fun createSpecExecutorDelegate(
   context: EngineContext,
): SpecExecutorDelegate = DefaultSpecExecutorDelegate(context)
