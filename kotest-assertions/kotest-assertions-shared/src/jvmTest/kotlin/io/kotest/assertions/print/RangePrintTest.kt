package io.kotest.assertions.print

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class RangePrintTest : FunSpec() {
   init {
      test("IntRangePrint should print the string presentation") {
         IntRangePrint.print((Int.MIN_VALUE until 1), 0) shouldBe Printed("-2147483648..0")
      }
      test("LongRangePrint should print the string presentation") {
         LongRangePrint.print(1L until 10L, 0) shouldBe Printed("1..9")
      }
      test("UIntRange should print the string presentation") {
         UIntRangePrint.print((1.toUInt()..6.toUInt()), 0) shouldBe Printed("1..6")
      }
      test("ULongRangePrint should print the string presentation") {
         ULongRangePrint.print(1L.toULong() until 10L.toULong(), 0) shouldBe Printed("1..9")
      }
      test("CharRangePrint should print the string presentation") {
         CharRangePrint.print('a'..'z', 0) shouldBe Printed("a..z")
      }
      test("ranges should use range prints") {
         shouldThrowAny {
            (Int.MIN_VALUE until 1) shouldBe (Int.MIN_VALUE until 2)
         }.message shouldContain "expected:<-2147483648..1> but was:<-2147483648..0>"
      }
   }
}
