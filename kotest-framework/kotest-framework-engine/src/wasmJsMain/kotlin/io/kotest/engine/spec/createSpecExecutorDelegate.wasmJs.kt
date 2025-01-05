package io.kotest.engine.spec

import io.kotest.common.ExperimentalKotest
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.kotlinJsTestFrameworkAvailable

@Suppress("DEPRECATION")
@ExperimentalKotest
internal actual fun createSpecExecutorDelegate(
   context: EngineContext,
): SpecExecutorDelegate =
   if (kotlinJsTestFrameworkAvailable()) {
      KotlinJsTestSpecExecutorDelegate(context)
   } else {
      DefaultSpecExecutorDelegate(context)
   }
