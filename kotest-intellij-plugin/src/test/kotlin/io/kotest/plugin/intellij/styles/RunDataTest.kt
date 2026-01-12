package io.kotest.plugin.intellij.styles

import io.kotest.matchers.shouldBe
import io.kotest.plugin.intellij.run.idea.RunData
import io.kotest.plugin.intellij.run.idea.suggestedName
import junit.framework.TestCase

class RunDataTest : TestCase() {

   fun `test work for packageName`() {
      RunData(null, null, "com.sksamuel.foo").suggestedName() shouldBe "All tests in 'com.sksamuel.foo'"
      RunData("", "", "com.sksamuel.foo").suggestedName() shouldBe "All tests in 'com.sksamuel.foo'"
   }

   fun `test work for spec without test`() {
      RunData("myspec", null, null).suggestedName() shouldBe "myspec"
      RunData("myspec", "", null).suggestedName() shouldBe "myspec"
   }

   fun `test return null if spec is null or blank and package is blank`() {
      RunData(null, "this is a test", null).suggestedName() shouldBe null
      RunData(null, "this is a test", null).suggestedName() shouldBe null
      RunData("", null, null).suggestedName() shouldBe null
      RunData("", null, null).suggestedName() shouldBe null
   }

   fun `test work for spec and test`() {
      RunData("myspec", "this is a test", null).suggestedName() shouldBe "myspec: this is a test"
   }

   fun `test suggested name should strip --`() {
      RunData("myspec", "this is a context -- and this is a test", null).suggestedName() shouldBe "myspec: this is a context and this is a test"
   }
}
