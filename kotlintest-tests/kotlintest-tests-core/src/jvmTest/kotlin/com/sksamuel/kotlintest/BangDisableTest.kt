package com.sksamuel.kotlintest

import io.kotlintest.fail
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.FunSpec
import io.kotlintest.specs.StringSpec
import io.kotlintest.specs.WordSpec

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
    System.setProperty("kotlintest.bang.disable", "true")
    "!allow this test to run" {
      run = true
    }
    System.getProperties().remove("kotlintest.bang.disable")
    run.shouldBeTrue()
  }
})