package com.sksamuel.kotest.property.arbitrary

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.factor
import io.kotest.property.arbitrary.take

@EnabledIf(LinuxOnlyGithubCondition::class)
class FactorTest : FunSpec({
   test("factors of k") {
      Arb.factor(99).take(100).forEach { 99 % it shouldBe 0 }
   }

   test("factor(k) can produce k itself") {
      Arb.factor(99).take(2000).toSet().shouldContain(99)
   }

   test("factor(1) does not throw") {
      shouldNotThrowAny { Arb.factor(1).take(10).toList() }
   }
})
