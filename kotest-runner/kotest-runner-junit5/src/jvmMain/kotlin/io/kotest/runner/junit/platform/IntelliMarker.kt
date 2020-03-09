package io.kotest.runner.junit.platform

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

interface IntelliMarker {
  @EnabledIfSystemProperty(named = "wibble", matches = "wobble")
  @TestFactory
  fun primer() {
  }
}
