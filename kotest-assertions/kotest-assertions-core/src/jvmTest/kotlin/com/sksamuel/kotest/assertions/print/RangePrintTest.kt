package com.sksamuel.kotest.assertions.print

import io.kotest.assertions.print.CharRangePrint
import io.kotest.assertions.print.IntRangePrint
import io.kotest.assertions.print.LongRangePrint
import io.kotest.assertions.print.Printed
import io.kotest.assertions.print.UIntRangePrint
import io.kotest.assertions.print.ULongRangePrint
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class RangePrintTest : FunSpec() {
   init {
      test("IntRangePrint should print the string presentation") {
         IntRangePrint.print((Int.MIN_VALUE until 1)) shouldBe Printed("-2147483648..0", IntRange::class)
      }
      test("LongRangePrint should print the string presentation") {
         LongRangePrint.print(1L until 10L) shouldBe Printed("1..9", LongRange::class)
      }
      test("UIntRange should print the string presentation") {
         UIntRangePrint.print((1.toUInt()..6.toUInt())) shouldBe Printed("1..6", UIntRange::class)
      }
      test("ULongRangePrint should print the string presentation") {
         ULongRangePrint.print(1L.toULong() until 10L.toULong()) shouldBe Printed("1..9", ULongRange::class)
      }
      test("CharRangePrint should print the string presentation") {
         CharRangePrint.print('a'..'z') shouldBe Printed("a..z", CharRange::class)
      }
      test("ranges should use range prints") {
         shouldThrowAny {
            (Int.MIN_VALUE until 1) shouldBe (Int.MIN_VALUE until 2)
         }.message shouldContain "expected:<-2147483648..1> but was:<-2147483648..0>"
      }
   }
}
