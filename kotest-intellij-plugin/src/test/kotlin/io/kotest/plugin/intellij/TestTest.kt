package io.kotest.plugin.intellij

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtPsiFactory

// odd class name, but the data class is called Test :/
class TestTest : LightJavaCodeInsightFixtureTestCase() {

   private fun createTest(
      name: String,
      parent: Test? = null,
      prefix: String? = null
   ): Test {
      val factory = KtPsiFactory(project)
      val spec: KtClass = factory.createClass("class MyTestClass { fun hello() {} }")
      return Test(
         name = TestName(prefix = prefix, name = name, interpolated = false),
         parent = parent,
         specClassName = spec,
         testType = TestType.Test,
         xdisabled = false,
         psi = spec,
         isDataTest = false
      )
   }

   fun `test path should flatten multiline test name to single line`() {
      val test = createTest("""
         this is a test
         that spans multiple
         lines
      """.trimIndent())

      test.path().map { it.name } shouldBe listOf("this is a test that spans multiple lines")
   }

   fun `test path should collapse multiple spaces to single space`() {
      val test = createTest("this    has     many    spaces")

      test.path().map { it.name } shouldBe listOf("this has many spaces")
   }

   fun `test path should trim leading and trailing whitespace`() {
      val test = createTest("   trimmed name   ")

      test.path().map { it.name } shouldBe listOf("trimmed name")
   }

   fun `test path should not include prefix`() {
      val test = createTest("a condition", prefix = "Given: ")

      test.path().map { it.name } shouldBe listOf("a condition")
   }

   fun `test path should handle tabs and newlines`() {
      val test = createTest("test\twith\ttabs\nand\nnewlines")

      test.path().map { it.name } shouldBe listOf("test with tabs and newlines")
   }

   fun `test path should handle empty lines in multiline string`() {
      val test = createTest("""
         first line

         third line
      """.trimIndent())

      test.path().map { it.name } shouldBe listOf("first line third line")
   }

   fun `test path should include parent path entries`() {
      val grandparent = createTest("grandparent test")
      val parent = createTest("parent test", parent = grandparent)
      val child = createTest("child test", parent = parent)

      child.path().map { it.name } shouldBe listOf("grandparent test", "parent test", "child test")
   }

   fun `test path should flatten all entries in nested hierarchy`() {
      val parent = createTest("""
         parent
         multiline
      """.trimIndent())
      val child = createTest("""
         child
         multiline
      """.trimIndent(), parent = parent)

      child.path().map { it.name } shouldBe listOf("parent multiline", "child multiline")
   }
}
