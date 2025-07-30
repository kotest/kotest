package com.sksamuel.kotest.eq

import io.kotest.assertions.eq.isDataClassInstance
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotStartWith
import io.kotest.matchers.string.shouldStartWith
import org.junit.jupiter.api.assertThrows

data class DataClass1(val a: Int, val b: Float)
data class DataClass2(val x: Int, val y: Float, val z: DataClass1)
data class DataClass3(val x: Int, val y: DataClass2, val z: Double)

data class DataClassWithMultipleConstructors(val a: Int, val b: Float) {
   private var c: String = "";

   constructor(a: Int, b: Float, c: String) : this(a, b) {
      this.c = c
   }
}

data class CircularDataClass(val num: Int, val nested: CircularDataClass?)

class RegularClass(val a: Int, val b: Float)

class DataClassEqTest : StringSpec({

   "Data class instances are determined to be dataclasses" {
      isDataClassInstance(DataClass1(1, 3.4F)) shouldBe true
   }

   "Non data class instances are determined as not dataclasses" {
      isDataClassInstance(RegularClass(1, 3.4F)) shouldBe false
   }

   "Simple detailed diffs are shown" {
      val throwable = assertThrows<Throwable> { DataClass1(1, 3.4F) shouldBe DataClass1(2, 3.5F) }

      throwable.message shouldStartWith """
         data class diff for com.sksamuel.kotest.eq.DataClass1
         ├ a: expected:<2> but was:<1>
         └ b: expected:<3.5f> but was:<3.4f>
         """.trimIndent()
   }

   "Only differing fields are shown in the diff" {
      val throwable = assertThrows<Throwable> {
         DataClass1(1, 3.14F) shouldBe DataClass1(2, 3.14F)
      }

      throwable.message shouldStartWith """
         data class diff for com.sksamuel.kotest.eq.DataClass1
         └ a: expected:<2> but was:<1>
         """.trimIndent()
   }

   "Nested detailed diffs are shown" {
      val actual = DataClass2(1, 3.4F, DataClass1(2, 4.6F))
      val expected = DataClass2(2, 4.4F, DataClass1(99, 7.6F))

      val throwable = assertThrows<Throwable> {
         actual shouldBe expected
      }
      throwable.message shouldStartWith """
         data class diff for com.sksamuel.kotest.eq.DataClass2
         ├ x: expected:<2> but was:<1>
         ├ y: expected:<4.4f> but was:<3.4f>
         └ z: data class diff for com.sksamuel.kotest.eq.DataClass1
            ├ a: expected:<99> but was:<2>
            └ b: expected:<7.6f> but was:<4.6f>
         """.trimIndent()
   }

   "Nested diffs are shown with correct indentation" {
      val actual = DataClass3(88, DataClass2(1, 3.4F, DataClass1(2, 4.6F)), 44.4)
      val expected = DataClass3(99, DataClass2(2, 4.4F, DataClass1(99, 7.6F)), 44.6)

      val throwable = assertThrows<Throwable> {
         actual shouldBe expected
      }
      throwable.message shouldStartWith """
         data class diff for com.sksamuel.kotest.eq.DataClass3
         ├ x: expected:<99> but was:<88>
         ├ y: data class diff for com.sksamuel.kotest.eq.DataClass2
         │  ├ x: expected:<2> but was:<1>
         │  ├ y: expected:<4.4f> but was:<3.4f>
         │  └ z: data class diff for com.sksamuel.kotest.eq.DataClass1
         │     ├ a: expected:<99> but was:<2>
         │     └ b: expected:<7.6f> but was:<4.6f>
         └ z: expected:<44.6> but was:<44.4>
         """.trimIndent()
   }

   "Only properties in the primary constructor are used in the diff" {
      DataClassWithMultipleConstructors(1, 2.2F, "hello") shouldBe
         DataClassWithMultipleConstructors(1, 2.2F, "goodbye")
   }

   "Only print differences of properties in the primary constructor in the diff" {
      val actual = DataClassWithMultipleConstructors(1, 2.2F, "hello")
      val expected = DataClassWithMultipleConstructors(2, 2.2F, "goodbye")

      val throwable = assertThrows<Throwable> { actual shouldBe expected }

      throwable.message shouldStartWith """
         data class diff for com.sksamuel.kotest.eq.DataClassWithMultipleConstructors
         └ a: expected:<2> but was:<1>
         """.trimIndent()
   }

   "Data class diffs are disabled when data class nesting is greater than 10 references deep" {
      val actual = (0..10).fold(null) { acc: CircularDataClass?, i ->
         CircularDataClass(i, acc)
      }
      val expected = (-1..11).fold(null) { acc: CircularDataClass?, i ->
         CircularDataClass(i, acc)
      }

      val throwable = assertThrows<Throwable> { actual shouldBe expected }

      throwable.message shouldNotStartWith "data class diff"
   }
})

class `DataClassEq AssertionConfig Tests` : StringSpec({

   afterTest { (_, _) ->
      System.setProperty("kotest.assertions.show-data-class-diffs", "true")
   }

   "Data class diffs can be disabled with a system property" {
      System.setProperty("kotest.assertions.show-data-class-diffs", "false")

      val throwable = assertThrows<Throwable> { DataClass1(1, 3.4F) shouldBe DataClass1(2, 3.5F) }

      throwable.message shouldBe "expected:<DataClass1(a=2, b=3.5)> but was:<DataClass1(a=1, b=3.4)>"
   }
})

data class CustomException(private val value: String) : Exception()
