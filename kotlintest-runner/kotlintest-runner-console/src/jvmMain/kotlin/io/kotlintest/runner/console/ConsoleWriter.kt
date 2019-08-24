package io.kotlintest.runner.console

import io.kotlintest.runner.jvm.TestEngineListener

interface ConsoleWriter : TestEngineListener {
  fun hasErrors(): Boolean
}