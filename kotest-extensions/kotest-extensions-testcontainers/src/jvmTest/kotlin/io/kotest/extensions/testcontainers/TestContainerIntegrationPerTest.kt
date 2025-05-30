package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class TestContainerIntegrationPerTest : StringSpec({

   val testStartable = configurePerTest(TestStartable())

   "start count for first test should be one" {
      testStartable.startCount shouldBe 1
   }

   "start count for second test should be two" {
      testStartable.startCount shouldBe 2
   }
})
