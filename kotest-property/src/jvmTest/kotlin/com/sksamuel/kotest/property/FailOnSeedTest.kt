package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.checkAll

@EnabledIf(LinuxOnlyGithubCondition::class)
class FailOnSeedTest : FunSpec() {
   init {
      test("property test should fail if seed is specified when noSeed mode is true") {
         PropertyTesting.failOnSeed = true
         shouldThrowAny {
            checkAll<String, String>(PropTestConfig(seed = 1231312)) { a, b -> }
         }.message shouldBe """A seed is specified on this property-test and failOnSeed is true"""
         PropertyTesting.failOnSeed = false
      }
   }
}
