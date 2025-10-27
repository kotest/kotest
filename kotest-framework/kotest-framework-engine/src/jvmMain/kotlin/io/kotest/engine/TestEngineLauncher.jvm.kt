package io.kotest.engine

import io.kotest.common.Platform
import io.kotest.engine.listener.TestEngineListener

// listeners will be setup by the gradle plugins / junit platform
internal actual fun Platform.listeners(): List<TestEngineListener> = emptyList()
