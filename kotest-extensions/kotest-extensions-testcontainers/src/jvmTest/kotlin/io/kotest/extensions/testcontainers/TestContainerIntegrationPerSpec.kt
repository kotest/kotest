package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestContainerIntegrationPerSpec : StringSpec({

   val testStartable = configurePerSpec(TestStartable())

   "start count for first test should be one" {
      testStartable.startCount shouldBe 1
   }

   "start count for second test should also be one" {
      testStartable.startCount shouldBe 1
   }
})
