package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.az

@EnabledIf(LinuxCondition::class)
class CharTest : FunSpec() {
   init {
      test("Exhaustive.az() should return all chars in a..z") {
         Exhaustive.az().values.toSet().shouldHaveSize(26)
         ('a'..'z').forEach {
            Exhaustive.az().values.shouldContain(it)
         }
      }
   }
}
