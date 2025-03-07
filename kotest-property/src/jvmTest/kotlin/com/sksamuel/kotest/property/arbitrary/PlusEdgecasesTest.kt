package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.edgecases
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.plusEdgecases

@EnabledIf(LinuxCondition::class)
class PlusEdgecasesTest: StringSpec() {
   private val intArb = Arb.Companion.int(-50..50)

   init {
       "add edgecases to intArb while keeping the original ones" {
          val edgecases = intArb.edgecases().toList()
          val additionalEdgecases = listOf(-42, 42)
          val newEdgecases = intArb.plusEdgecases(additionalEdgecases).edgecases().toList()
          newEdgecases.containsAll(edgecases) shouldBe true
          newEdgecases.containsAll(additionalEdgecases) shouldBe true
       }
   }
}
