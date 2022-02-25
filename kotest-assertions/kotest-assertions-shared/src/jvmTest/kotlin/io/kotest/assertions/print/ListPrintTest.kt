package io.kotest.assertions.print

import io.kotest.assertions.ConfigValue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class ListPrintTest : FunSpec({
   context("when a collection size limit has been set") {
      context("and a description of the source of the limit is available") {
         val config = TestConfigValue(5, "the test config value")
         val printer = ListPrint<String>(config)

         test("should format an empty list correctly") {
            printer.print(emptyList(), 0).value shouldBe "[]"
         }

         test("should format a single item list correctly") {
            printer.print(listOf("value"), 0).value shouldBe """["value"]"""
         }

         test("should include all items when formatting a list shorter than the provided limit") {
            printer.print(listOf("a", "b", "c"), 0).value shouldBe """["a", "b", "c"]"""
         }

         test("should include all items when formatting a list with the same length as the provided limit") {
            printer.print(listOf("a", "b", "c", "d", "e"), 0).value shouldBe """["a", "b", "c", "d", "e"]"""
         }

         test("should only include a limited number of items when formatting a list longer than the provided limit") {
            printer.print(listOf("a", "b", "c", "d", "e", "f", "g"), 0).value shouldBe """["a", "b", "c", "d", "e", ...and 2 more (set the test config value to see more / less items)]"""
         }
      }

      context("and a description of the source of the limit is not available") {
         val config = TestConfigValue(5, null)
         val printer = ListPrint<String>(config)

         test("should not include a hint on how to configure the limit when the list is longer than the limit") {
            printer.print(listOf("a", "b", "c", "d", "e", "f", "g"), 0).value shouldBe """["a", "b", "c", "d", "e", ...and 2 more]"""
         }
      }
   }

   context("when the collection size limit has been disabled") {
      val config = TestConfigValue(-1, "the test config value")
      val printer = ListPrint<String>(config)

      test("should format an empty list correctly") {
         printer.print(emptyList(), 0).value shouldBe "[]"
      }

      test("should format a single item list correctly") {
         printer.print(listOf("value"), 0).value shouldBe """["value"]"""
      }

      test("should include all items when formatting a long list") {
         printer.print(listOf("a", "b", "c", "d", "e", "f", "g"), 0).value shouldBe """["a", "b", "c", "d", "e", "f", "g"]"""
      }
   }
})

class TestConfigValue<T>(override val value: T, override val sourceDescription: String?) : ConfigValue<T>
