package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.js.KotlinJsSpecExecutor

internal actual fun specExecutor(context: EngineContext, spec: Spec): SpecExecutor = KotlinJsSpecExecutor(context)
