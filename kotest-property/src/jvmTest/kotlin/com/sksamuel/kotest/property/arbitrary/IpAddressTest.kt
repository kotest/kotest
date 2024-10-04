package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeBetween
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.ipAddressV4
import io.kotest.property.arbitrary.ipAddressV6
import io.kotest.property.checkAll

@EnabledIf(LinuxCondition::class)
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

      test("Arb.ipAddressV6 should generate in a:b:c:d:e:f:g:h format") {
         checkAll(100, Arb.ipAddressV6()) { ip ->
            ip.shouldMatch("([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}".toRegex())
         }
      }

      test("Arb.ipAddressV6 should generate each component in the range 0-65535") {
         checkAll(100, Arb.ipAddressV6()) { ip ->
            val result =
               "([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}:([0-9,A-F]){1,4}".toRegex()
                  .matchEntire(ip)!!
            result.groupValues[1].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[2].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[3].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[4].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[5].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[6].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[7].toInt(16).shouldBeBetween(0, 65535)
            result.groupValues[8].toInt(16).shouldBeBetween(0, 65535)
         }
      }
   }
}
