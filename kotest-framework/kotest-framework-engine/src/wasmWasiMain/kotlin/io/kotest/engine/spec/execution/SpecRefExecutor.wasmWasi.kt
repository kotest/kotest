package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.interceptors.EngineContext

internal actual fun specExecutor(context: EngineContext, spec: Spec): SpecExecutor = SingleInstanceSpecExecutor(context)
