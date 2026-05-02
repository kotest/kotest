package io.kotest.runner.junit.platform

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TruncateTestNameTest : FunSpec({

   test("name shorter than max length is returned unchanged") {
      truncateTestName("short name") shouldBe "short name"
   }

   test("empty string is returned unchanged") {
      truncateTestName("") shouldBe ""
   }

   test("name exactly at max length is returned unchanged") {
      val name = "a".repeat(MAX_TRUNCATED_NAME_LENGTH)
      truncateTestName(name) shouldBe name
   }

   test("name one character over max length is truncated with ellipsis") {
      val name = "a".repeat(MAX_TRUNCATED_NAME_LENGTH + 1)
      val truncated = truncateTestName(name)
      truncated shouldBe "a".repeat(MAX_TRUNCATED_NAME_LENGTH - 3) + "..."
      truncated.length shouldBe MAX_TRUNCATED_NAME_LENGTH
   }

   test("long name is truncated to max length with ellipsis") {
      val name = "a".repeat(MAX_TRUNCATED_NAME_LENGTH * 2)
      val truncated = truncateTestName(name)
      truncated.length shouldBe MAX_TRUNCATED_NAME_LENGTH
      truncated.endsWith("...") shouldBe true
   }

   test("truncated name ends with ellipsis") {
      val name = "Given a user is logged in and has an active subscription with premium features enabled"
      val truncated = truncateTestName(name)
      truncated.length shouldBe MAX_TRUNCATED_NAME_LENGTH
      truncated.endsWith("...") shouldBe true
      truncated shouldBe name.take(MAX_TRUNCATED_NAME_LENGTH - 3) + "..."
   }

   test("does not split a surrogate pair at the truncation boundary") {
      // 🎉 (U+1F389) is a supplementary code point encoded as a UTF-16 surrogate pair.
      // Engineer the input so the truncation point (index MAX_TRUNCATED_NAME_LENGTH - 3 = 45)
      // lands between the high and low surrogate, which would have split the pair.
      val emoji = "🎉" // 🎉
      val prefix = "a".repeat(MAX_TRUNCATED_NAME_LENGTH - 3 - 1) // 44 chars → boundary at 45
      val name = prefix + emoji + "tail"
      val truncated = truncateTestName(name)
      // A naive `name.take(45)` would split the pair and leave a dangling high surrogate.
      // The fix drops the dangling high surrogate, producing one fewer char than the cap.
      truncated.endsWith("...") shouldBe true
      truncated.last().isLowSurrogate() shouldBe false
      truncated.toCharArray().none { it.isHighSurrogate() } shouldBe true
   }
})
