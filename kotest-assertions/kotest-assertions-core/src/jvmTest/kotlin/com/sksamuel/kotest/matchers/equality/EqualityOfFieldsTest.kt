package com.sksamuel.kotest.matchers.equality

import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.equality.FieldComparison
import io.kotest.matchers.equality.comparisonToUse
import io.kotest.matchers.equality.isEnum
import io.kotest.matchers.equality.typeIsJavaOrKotlinBuiltIn
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.time.DayOfWeek

class EqualityOfFieldsTest: WordSpec() {
   init {
      "comparisonToUse" should {
         "handle nulls" {
            comparisonToUse(null, "Apple", listOf()) shouldBe FieldComparison.DEFAULT
            comparisonToUse("Apple", null, listOf()) shouldBe FieldComparison.DEFAULT
         }
         "handle enums" {
            comparisonToUse(DayOfWeek.MONDAY, Mug("blue", 11), listOf()) shouldBe FieldComparison.DEFAULT
            comparisonToUse(Mug("blue", 11), DayOfWeek.MONDAY, listOf()) shouldBe FieldComparison.DEFAULT
         }
         "handle built-in type" {
            comparisonToUse(BigDecimal.ONE, Mug("blue", 11), listOf()) shouldBe FieldComparison.DEFAULT
            comparisonToUse(Mug("blue", 11), BigDecimal.ONE, listOf()) shouldBe FieldComparison.DEFAULT
         }
         "use RECURSIVE" {
            comparisonToUse(Mug("blue", 12), Mug("blue", 11), listOf()) shouldBe FieldComparison.RECURSIVE
         }
         "do not use RECURSIVE if classes are different" {
            comparisonToUse(Mug("blue", 12), Cup("blue", 11), listOf()) shouldBe FieldComparison.DEFAULT
         }
         "override with custom list of types" {
            val className = "com.sksamuel.kotest.matchers.equality.Mug"
            comparisonToUse(Mug("blue", 12), Mug("blue", 11), listOf(className)) shouldBe FieldComparison.DEFAULT
         }
         "handle List" {
            comparisonToUse(listOf(1), listOf(12), listOf()) shouldBe FieldComparison.LIST
         }
         "handle Map" {
            comparisonToUse(mapOf(2 to "two"), mapOf(1 to "one"), listOf()) shouldBe FieldComparison.MAP
         }
         "handle Set" {
            comparisonToUse(setOf(1), setOf(12), listOf()) shouldBe FieldComparison.SET
         }
      }
      "isEnum" should {
         "handle null" {
            isEnum(null) shouldBe false
         }
         "handle Kotlin enum" {
            isEnum(ReflectionKtTest.SimpleEnum.ONE) shouldBe true
         }
         "handle enum from Java standard library" {
            isEnum(DayOfWeek.TUESDAY) shouldBe true
         }
         "handle non-enum" {
            isEnum("Something") shouldBe false
         }
      }
      "typeIsJavaOrKotlinBuiltIn" should {
         "true for String" {
            typeIsJavaOrKotlinBuiltIn("Any") shouldBe true
         }
         "true for Exception" {
            typeIsJavaOrKotlinBuiltIn(Exception("Oops!")) shouldBe true
         }
         "false for custom type" {
            typeIsJavaOrKotlinBuiltIn(ReflectionKtTest.Car("C1", 10000, 430)) shouldBe false
         }
      }
   }
}

internal data class Mug(
   val color: String,
   val capacity: Int
)

internal data class Cup(
   val color: String,
   val capacity: Int
)
