package io.kotest.engine

import io.kotest.common.Platform
import io.kotest.engine.listener.TestEngineListener

internal actual fun Platform.listeners(): List<TestEngineListener> = emptyList()
