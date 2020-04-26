package io.kotest.plugin.intellij.styles

import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.psi.buildSuggestedName
import junit.framework.TestCase

class UtilsKtTest : TestCase() {

   fun `test work for packageName`() {
      buildSuggestedName(null, null, "com.sksamuel.foo") shouldBe "All tests in 'com.sksamuel.foo'"
      buildSuggestedName("", "", "com.sksamuel.foo") shouldBe "All tests in 'com.sksamuel.foo'"
   }

   fun `test work for spec without test`() {
      buildSuggestedName("myspec", null, null) shouldBe "myspec"
      buildSuggestedName("myspec", "", null) shouldBe "myspec"
   }

   fun `test return null if spec is null or blank and package is blank`() {
      buildSuggestedName(null, "this is a test", null) shouldBe null
      buildSuggestedName(null, "this is a test", null) shouldBe null
      buildSuggestedName("", null, null) shouldBe null
      buildSuggestedName("", null, null) shouldBe null
   }

   fun `test work for spec and test`() {
      buildSuggestedName("myspec", "this is a test", null) shouldBe "myspec: this is a test"
   }
}
