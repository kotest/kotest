package io.kotest.engine.spec.execution

import io.kotest.core.spec.Spec
import io.kotest.engine.TestEngineContext

internal actual fun specExecutor(context: TestEngineContext, spec: Spec): SpecExecutor =
   SingleInstanceSpecExecutor(context)
