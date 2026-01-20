package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineContext
import io.kotest.engine.js.KotlinJsSpecExecutor

internal actual fun specExecutor(context: TestEngineContext, spec: Spec): SpecExecutor = KotlinJsSpecExecutor(context)
