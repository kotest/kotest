package com.sksamuel.kotest.property

import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.default
import io.kotest.property.checkAll

class ArbDefaultsTest : WordSpec({

   "Gen.default" should {
      "generate the defaults for list" {

         val gen = Arb.default<List<Int>>()
         checkAll(10, gen) { list ->
            list.forAll { i ->
               i.shouldBeInstanceOf<Int>()
            }
         }
      }

      "generate the defaults for set" {

         val gen = Arb.default<Set<String>>()
         checkAll(gen) { inst ->
            inst.forAll { i ->
               i.shouldBeInstanceOf<String>()
            }
         }
      }
   }
})
