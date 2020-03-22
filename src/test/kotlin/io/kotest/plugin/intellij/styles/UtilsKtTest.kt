package io.kotest.plugin.intellij.styles

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class UtilsKtTest : StringSpec() {

  init {

    "buildSuggestedName should work for spec only" {
      buildSuggestedName("myspec", null) shouldBe "myspec"
      buildSuggestedName("myspec", "") shouldBe "myspec"
    }

    "buildSuggestedName should return null if spec is nul or blank" {
      buildSuggestedName(null, "this is a test") shouldBe null
      buildSuggestedName(null, "this is a test") shouldBe null
      buildSuggestedName("", null) shouldBe null
      buildSuggestedName("", null) shouldBe null
    }

    "buildSuggestedName should work for spec and test" {
      buildSuggestedName("myspec", "this is a test") shouldBe "myspec: this is a test"
    }

  }

}