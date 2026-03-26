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
})
