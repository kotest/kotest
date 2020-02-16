package io.kotest.runner.junit5

import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

interface IntelliMarker {
  @EnabledIfSystemProperty(named = "wibble", matches = "wobble")
  @TestFactory
  fun primer() {
  }
}
