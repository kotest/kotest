package io.kotlintest

import io.kotlintest.matchers.shouldBe
import io.kotlintest.provided.ProjectConfig
import io.kotlintest.specs.WordSpec

class ConfigTest2 : WordSpec() {
  init {
    "TestCase config" should {
      "only run beforeAll once" {
        // we are testing this in two places and it should therefore be 1 in both places
        ProjectConfig.beforeAll shouldBe 1
      }
      "only run afterAll once" {
        // this test spec has not yet completed, and therefore this count should be 0
        // however, since we have two equal tests in two test specs, we can check
        // that afterAll is definitely not firing after a single suite is completed
        ProjectConfig.afterAll shouldBe 0
      }
    }
  }
}