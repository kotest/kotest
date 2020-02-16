package com.sksamuel.kotest

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class ProjectListenerTest : WordSpec() {
  init {
    "TestCase config" should {
      "only run beforeAll once" {
        // we are testing this in two places and it should therefore be 1 in both places
        TestProjectListener.beforeAll shouldBe 1
      }
      "only run afterAll once" {
        // this test spec has not yet completed, and therefore this count should be 0
        // we will also assert this in another test suite, where it should still be 0
        // but at that point at least _one_ test suite will have completed
        // so that will confirm it is not being fired after every spec
         TestProjectListener.afterAll shouldBe 0
      }
    }
  }
}
