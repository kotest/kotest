package com.sksamuel.kotest.engine.names

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.config.PackageUtils
import io.kotest.matchers.shouldBe

class PackageUtilsTest : FunSpec() {
   init {
      test("should throw exception for empty collection") {
         shouldThrow<IllegalArgumentException> {
            PackageUtils.commonPrefix(emptyList())
         }
      }

      test("should return the single string for collection with one element") {
         PackageUtils.commonPrefix(listOf("hello")) shouldBe "hello"
      }

      test("should return common package prefix for strings with shared package segments") {
         PackageUtils.commonPrefix(listOf("org.mypackage.service", "org.mypackage.repo", "org.mypackage.controllers")) shouldBe "org.mypackage"
      }

      test("should not match partial package segments") {
         PackageUtils.commonPrefix(listOf("com.example.sam", "com.example.sap")) shouldBe "com.example"
         PackageUtils.commonPrefix(listOf("prefix_a", "prefix_b", "prefix_c")) shouldBe ""
         PackageUtils.commonPrefix(listOf("kotest", "kotlin", "ko")) shouldBe ""
      }

      test("should return empty string when no common package prefix exists") {
         PackageUtils.commonPrefix(listOf("abc", "def", "ghi")) shouldBe ""
         PackageUtils.commonPrefix(listOf("test", "best")) shouldBe ""
      }

      test("should handle identical strings") {
         PackageUtils.commonPrefix(listOf("same", "same", "same")) shouldBe "same"
         PackageUtils.commonPrefix(listOf("com.example.same", "com.example.same")) shouldBe "com.example.same"
      }

      test("should handle strings where one is a prefix of another") {
         PackageUtils.commonPrefix(listOf("com.test", "com.test.sub1", "com.test.sub2")) shouldBe "com.test"
      }

      test("should handle empty strings in collection") {
         PackageUtils.commonPrefix(listOf("", "test", "testing")) shouldBe ""
         PackageUtils.commonPrefix(listOf("test", "", "testing")) shouldBe ""
      }

      test("should handle single segment packages") {
         PackageUtils.commonPrefix(listOf("org", "org")) shouldBe "org"
         PackageUtils.commonPrefix(listOf("org", "com")) shouldBe ""
      }
   }
}
