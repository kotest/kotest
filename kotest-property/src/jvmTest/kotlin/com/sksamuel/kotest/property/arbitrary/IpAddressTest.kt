package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.ipAddressV4
import io.kotest.property.checkAll

class IpAddressTest : FunSpec() {
   init {

      test("Arb.ipAddressV4 should generate in a.b.c.d format") {
         checkAll(100, Arb.ipAddressV4()) { ip ->
            ip.shouldMatch("\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}".toRegex())
         }
      }

      test("Arb.ipAddressV4 should generate each component in the range 0-255") {
         checkAll(100, Arb.ipAddressV4()) { ip ->
            val result = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})".toRegex().matchEntire(ip)!!
            result.groupValues[1].toInt().shouldBeBetween(0, 255)
            result.groupValues[2].toInt().shouldBeBetween(0, 255)
            result.groupValues[3].toInt().shouldBeBetween(0, 255)
            result.groupValues[4].toInt().shouldBeBetween(0, 255)
         }
      }
   }
}
