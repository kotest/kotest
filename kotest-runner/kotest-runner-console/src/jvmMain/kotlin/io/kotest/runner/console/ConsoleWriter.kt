package io.kotest.runner.console

import io.kotest.runner.jvm.TestEngineListener

interface ConsoleWriter : TestEngineListener {
  fun hasErrors(): Boolean
}