package com.sksamuel.kotest.engine.names

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.engine.config.PackageUtils
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class CommonPrefixTest : FunSpec() {
   init {
      test("should throw exception for empty collection") {
         shouldThrow<IllegalArgumentException> {
            PackageUtils.commonPrefix(emptyList())
         }
      }

      test("should return the single string for collection with one element") {
         PackageUtils.commonPrefix(listOf("hello")) shouldBe "hello"
      }

      test("should return common prefix for strings with shared prefix") {
         PackageUtils.commonPrefix(listOf("org.mypackage.service", "org.mypackage.repo", "org.mypackage.controllers")) shouldBe "org.mypackage."
         PackageUtils.commonPrefix(listOf("prefix_a", "prefix_b", "prefix_c")) shouldBe "prefix_"
         PackageUtils.commonPrefix(listOf("kotest", "kotlin", "ko")) shouldBe "ko"
      }

      test("should return empty string when no common prefix exists") {
         PackageUtils.commonPrefix(listOf("abc", "def", "ghi")) shouldBe ""
         PackageUtils.commonPrefix(listOf("test", "best")) shouldBe ""
      }

      test("should handle identical strings") {
         PackageUtils.commonPrefix(listOf("same", "same", "same")) shouldBe "same"
      }

      test("should handle strings where one is a prefix of another") {
         PackageUtils.commonPrefix(listOf("test", "test123", "test456")) shouldBe "test"
      }

      test("should handle empty strings in collection") {
         PackageUtils.commonPrefix(listOf("", "test", "testing")) shouldBe ""
         PackageUtils.commonPrefix(listOf("test", "", "testing")) shouldBe ""
      }

      test("should handle single character differences") {
         PackageUtils.commonPrefix(listOf("a", "ab", "abc")) shouldBe "a"
         PackageUtils.commonPrefix(listOf("xyz", "xy", "x")) shouldBe "x"
      }
   }
}
