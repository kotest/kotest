package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.*

class FlatMapTest : FunSpec() {
   init {
      test("flat map") {
         Arb.int(1..10).flatMap { Arb.string(it) }.take(15, RandomSource.seeded(3242344L)).toList() shouldBe
            listOf(
               "\"i S",
               "Twuy",
               "OiZ3",
               "6w7p",
               "`SY\\",
               "QMhd",
               ", ]s",
               "\$MEo",
               "vxF_",
               "!;S)",
               "KsQm",
               "mR9r",
               "!-Ov",
               "#!#&",
               "kke3"
            )
      }
   }
}
