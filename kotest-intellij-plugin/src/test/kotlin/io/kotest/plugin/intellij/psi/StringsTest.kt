package io.kotest.plugin.intellij.psi

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import io.kotest.matchers.shouldBe
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

class StringsTest : BasePlatformTestCase() {

   private fun parse(text: String): KtStringTemplateExpression {
      return KtPsiFactory(project).createExpression(text) as KtStringTemplateExpression
   }

   fun testPureLiteralReturnsTextAsIs() {
      parse("\"hello world\"").asString() shouldBe StringArg("hello world", false)
   }

   fun testEmptyStringReturnsEmpty() {
      parse("\"\"").asString() shouldBe StringArg("", false)
   }

   fun testEscapedDoubleQuoteIsUnescaped() {
      parse("\"say \\\"hi\\\"\"").asString() shouldBe StringArg("say \"hi\"", false)
   }

   fun testSimpleInterpolationIsReplacedWithWildcard() {
      // "hello $name" → "hello *"
      parse("\"hello \$name\"").asString() shouldBe StringArg("hello *", true)
   }

   fun testBlockInterpolationIsReplacedWithWildcard() {
      // "hello ${name.uppercase()}" → "hello *"
      parse("\"hello \${name.uppercase()}\"").asString() shouldBe StringArg("hello *", true)
   }

   fun testMultipleSimpleInterpolationsAreEachReplacedWithWildcard() {
      // "test $a and $b" → "test * and *"
      parse("\"test \$a and \$b\"").asString() shouldBe StringArg("test * and *", true)
   }

   fun testInterpolationAtStartIsReplacedWithWildcard() {
      // "$prefix suffix" → "* suffix"
      parse("\"\$prefix suffix\"").asString() shouldBe StringArg("* suffix", true)
   }

   fun testInterpolationAtEndIsReplacedWithWildcard() {
      // "prefix $suffix" → "prefix *"
      parse("\"prefix \$suffix\"").asString() shouldBe StringArg("prefix *", true)
   }

   fun testOnlyInterpolationIsReplacedWithSingleWildcard() {
      // "$name" → "*"
      parse("\"\$name\"").asString() shouldBe StringArg("*", true)
   }

   fun testMixedLiteralAndMultipleBlockInterpolations() {
      // "from ${a.x} to ${b.y}" → "from * to *"
      parse("\"from \${a.x} to \${b.y}\"").asString() shouldBe StringArg("from * to *", true)
   }
}
