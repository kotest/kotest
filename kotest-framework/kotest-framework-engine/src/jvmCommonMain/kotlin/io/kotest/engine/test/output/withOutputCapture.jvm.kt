package io.kotest.engine.test.output

import io.kotest.common.JVMOnly

@JVMOnly
internal actual suspend fun <T> withOutputCapture(block: suspend () -> T): T =
   StandardStreamCapture.withEngineScope(block)
