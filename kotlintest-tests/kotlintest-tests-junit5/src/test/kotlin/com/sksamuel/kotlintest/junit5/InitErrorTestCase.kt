package com.sksamuel.kotlintest.junit5

import io.kotlintest.specs.StringSpec

class InitErrorSpec : StringSpec() {
  init {
    // we only want to throw this when are testing it via TestEngineTest above
    // and not through normal discovery of all tests
    if (System.getProperty("KotlinTestEngineTest") == "true")
      throw Throwable("kaboom")
  }
}