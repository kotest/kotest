package com.sksamuel.kotlintest.tests

import io.kotlintest.fail
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.StringSpec

class BandDisableFunSpec : FunSpec({
  test("!should not run") {
    fail("boom")
  }
})

class BandDisableStringSpec : StringSpec({
  "!should not run" {
    fail("boom")
  }
})