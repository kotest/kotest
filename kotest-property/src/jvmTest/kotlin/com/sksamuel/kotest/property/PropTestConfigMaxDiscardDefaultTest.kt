package com.sksamuel.kotest.property

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting

@EnabledIf(LinuxOnlyGithubCondition::class)
class PropTestConfigMaxDiscardDefaultTest : FunSpec({

   test("PropTestConfig.maxDiscardPercentage default should track PropertyTesting.maxDiscardPercentage") {
      val original = PropertyTesting.maxDiscardPercentage
      try {
         PropertyTesting.maxDiscardPercentage = 73
         PropTestConfig().maxDiscardPercentage shouldBe 73
      } finally {
         PropertyTesting.maxDiscardPercentage = original
      }
   }
})
