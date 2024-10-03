package com.sksamuel.kotest

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@EnabledIf(LinuxCondition::class)
class IterableShouldBeTest : DescribeSpec() {
   init {
      describe("shouldBe") {
         it("list / list") {
            listOf(1, 2, 3) shouldBe listOf(1, 2, 3)
            listOf(1, 2, 3) shouldNotBe listOf(5, 6, 7)
            listOf<Int>() shouldBe emptyList()
            listOf(1) shouldNotBe emptyList<Int>()
            emptyList<Int>() shouldNotBe listOf(1)
         }
         it("list / set") {
            listOf(1) shouldBe setOf(1)
            listOf(1, 2) shouldBe setOf(1, 2)
            listOf(1, 2) shouldNotBe setOf(2, 1)
            emptySet<Int>() shouldBe setOf()
            listOf(1) shouldNotBe emptySet<Int>()
            setOf(1) shouldNotBe listOf(1, 1, 1)
            emptySet<Int>() shouldNotBe listOf(1)
            emptyList<Int>() shouldNotBe setOf(1)
         }
         it("set / set") {
            setOf(1) shouldBe setOf(1)
            setOf(1) shouldBe setOf(1, 1, 1)
            setOf(1, 2, 3) shouldBe setOf(3, 2, 1)
            emptySet<Int>() shouldBe setOf()
            setOf<Int>() shouldBe emptySet()
            setOf(1) shouldNotBe emptySet<Int>()
            emptySet<Int>() shouldNotBe setOf(1)
            setOf(1, 2) shouldNotBe setOf(1)
            setOf(1, 2) shouldNotBe setOf(1, 3)
         }
         it("linked hash set / list") {
            LinkedHashSet(setOf(1, 2, 3)) shouldBe listOf(1, 2, 3)
            listOf(1, 2, 3) shouldBe LinkedHashSet(setOf(1, 2, 3))
            LinkedHashSet<Int>() shouldBe emptyList()
            emptyList<Int>() shouldBe LinkedHashSet<Int>()
         }
         it("array / array") {
            arrayOf(1, 2, 3) shouldBe arrayOf(1, 2, 3)
            arrayOf(1, 2, 3) shouldNotBe arrayOf(5, 6, 7)
            arrayOf<Int>() shouldBe emptyArray()
            arrayOf(1) shouldNotBe emptyArray<Int>()
            emptyArray<Int>() shouldNotBe arrayOf(1)
         }
         it("array / list") {
            arrayOf(1, 2, 3) shouldBe listOf(1, 2, 3)
            listOf(1, 2, 3) shouldNotBe listOf(5, 6, 7)
            arrayOf(1, 2, 3) shouldNotBe arrayOf(5, 6, 7)
            arrayOf<Int>() shouldBe emptyList<Int>()
            arrayOf(1) shouldNotBe emptyList<Int>()
            listOf(1) shouldNotBe emptyArray<Int>()
            emptyArray<Int>() shouldNotBe listOf(1)
            emptyList<Int>() shouldNotBe arrayOf(1)
         }
         it("array / java.util.ArrayList") {
            arrayOf(1, 2, 3) shouldBe java.util.ArrayList(listOf(1, 2, 3))
            arrayOf(1, 2, 3) shouldNotBe java.util.ArrayList(listOf(5, 6, 7))
            arrayOf<Int>() shouldBe java.util.ArrayList<Int>()
            arrayOf(1) shouldNotBe java.util.ArrayList<Int>()
            arrayOf(1) shouldNotBe java.util.ArrayList<Int>()
            emptyArray<Int>() shouldNotBe java.util.ArrayList(listOf(1))
         }
      }
   }
}
