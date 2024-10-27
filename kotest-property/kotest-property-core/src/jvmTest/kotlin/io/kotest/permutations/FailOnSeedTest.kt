package io.kotest.permutations

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class FailOnSeedTest : FunSpec() {
   init {
      test("property test should fail if seed is specified when failOnSeed is true") {
         shouldThrowAny {
            permutations {
               failOnSeed = true
               seed = 1234
               forEach {

               }
            }
         }.message shouldBe """A seed is specified on this property test and failOnSeed is true"""
      }
   }
}
