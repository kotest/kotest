package com.sksamuel.kotest.junit5

import io.kotest.specs.StringSpec

class InitErrorSpec : StringSpec() {
  init {
    // we only want to throw this when are testing it via TestEngineTest above
    // and not through normal discovery of all tests
    if (System.getProperty("KotestEngineTest") == "true")
      throw Throwable("kaboom")
  }
}