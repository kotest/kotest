@file:Suppress("DEPRECATION")

package com.sksamuel.kotest.property

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll
import io.kotest.property.forAll
import io.kotest.property.forNone
import io.kotest.property.withAssumptions

@EnabledIf(LinuxOnlyGithubCondition::class)
class NullableTest : FunSpec({
   test("forAll with implicit nullable arbitaries") {
      val iterations = 1000
      val classifications = forAll<Int?>(iterations, PropTestConfig(seed = 1)) { num ->
         classify(num == null, "null", "non-null")
         true
      }.classifications()
      classifications["null"] shouldBe 493
      classifications["non-null"] shouldBe 507
   }

   test("forNone with implicit nullable arbitraries") {
      val iterations = 1000
      val classifications = forNone<Int?>(iterations, PropTestConfig(seed = 1)) { num ->
         classify(num == null, "null", "non-null")
         false
      }.classifications()
      classifications["null"] shouldBe 493
      classifications["non-null"] shouldBe 507
   }

   test("checkAll with implicit nullable arbitraries") {
      val iterations = 1000
      val classifications = checkAll<Int?>(iterations, PropTestConfig(seed = 1)) { num ->
         classify(num == null, "null", "non-null")
      }.classifications()
      classifications["null"] shouldBe 493
      classifications["non-null"] shouldBe 507
   }

   test("checkAll with implicit nullable arbitraries with should not be null Assumption") {
      checkAll<Int?> { num ->
         withAssumptions(num != null) {
            num shouldNotBe null
         }
      }
   }

   test("checkAll with implicit nullable arbitraries with should be null Assumption") {
      checkAll<Int?> { num ->
         withAssumptions(num == null) {
            num shouldBe null
         }
      }
   }
})
