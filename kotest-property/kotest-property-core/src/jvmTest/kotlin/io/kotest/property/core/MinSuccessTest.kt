package io.kotest.property.core

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.ints

@EnabledIf(LinuxCondition::class)
class MinSuccessTest : FunSpec({

   test("forAll with minSuccess") {
      val message = shouldThrowAny {
         permutations {
            iterations = 1000
            minSuccess = 999
            maxFailures = 1000
            val a by gen { Exhaustive.ints(0..10) }
            forEach {
               a shouldBeLessThan 8
            }
         }
      }.message
      message shouldContain """Property failed after 8 attempts"""
      message shouldContain """Caused by: Property passed 8 times (minSuccess rate was 9)"""
   }
})
