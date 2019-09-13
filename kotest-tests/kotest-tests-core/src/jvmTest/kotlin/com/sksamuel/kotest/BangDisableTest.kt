package com.sksamuel.kotest

import io.kotest.assertions.fail
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.specs.FunSpec
import io.kotest.specs.StringSpec
import io.kotest.specs.WordSpec

class BangDisableFunTest : FunSpec() {
  init {
    test("!should not run") {
      fail("boom")
    }
  }
}

class BangDisableStringTest : StringSpec({
  "!should not run" {
    fail("boom")
  }
})

class BangOverridenWordTest : WordSpec({
  "setting system property to override bang" should {
    var run = false
    System.setProperty("kotest.bang.disable", "true")
    "!allow this test to run" {
      run = true
    }
    System.getProperties().remove("kotest.bang.disable")
    run.shouldBeTrue()
  }
})
