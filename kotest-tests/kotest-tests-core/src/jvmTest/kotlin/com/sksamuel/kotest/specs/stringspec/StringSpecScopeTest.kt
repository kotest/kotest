package com.sksamuel.kotest.specs.stringspec

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec

class StringSpecScopeTest : StringSpec() {
  init {
    "allowed" {
      shouldThrow<Exception> {
        "this should throw at runtime" {

        }
      }
    }
  }
}
