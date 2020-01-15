package io.kotest.specs

import io.kotest.core.specs.AbstractExpectSpec
import io.kotest.core.specs.AbstractFreeSpec
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.condition.EnabledIfSystemProperty

interface IntelliMarker {
  @EnabledIfSystemProperty(named = "wibble", matches = "wobble")
  @TestFactory
  fun primer() {
  }
}

abstract class AnnotationSpec(body: AbstractAnnotationSpec.() -> Unit = {}) : AbstractAnnotationSpec(body), IntelliMarker

abstract class ExpectSpec(body: AbstractExpectSpec.() -> Unit = {}) : AbstractExpectSpec(body), IntelliMarker

abstract class FreeSpec(body: AbstractFreeSpec.() -> Unit = {}) : AbstractFreeSpec(body), IntelliMarker
