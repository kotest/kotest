package io.kotest.engine.test.output

import io.kotest.core.spec.SpecRef
import io.kotest.engine.listener.TestEngineListener

internal actual suspend fun <T> withOutputCapture(block: suspend () -> T): T = block()

internal actual fun excludeFromOutputCapture(listener: TestEngineListener): TestEngineListener = listener

internal actual suspend fun <T> withSpecThreadOwnership(ref: SpecRef, enabled: Boolean, block: suspend () -> T): T =
   block()
