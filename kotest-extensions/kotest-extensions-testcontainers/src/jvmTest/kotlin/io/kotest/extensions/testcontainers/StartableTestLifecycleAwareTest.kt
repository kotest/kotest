package io.kotest.extensions.testcontainers

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

@EnabledIf(LinuxOnlyGithubCondition::class)
class StartableTestLifecycleAwareTest : StringSpec({

   val startableTestLifecycleAwareForPerTest = StartableTestLifecycleAware()
   val startableTestLifecycleAwareForPerSpec = StartableTestLifecycleAware()

   extensions(startableTestLifecycleAwareForPerTest.perTest(), startableTestLifecycleAwareForPerSpec.perSpec())

   "beforeTestCount for first test should be one" {
      startableTestLifecycleAwareForPerTest.testDescriptions shouldHaveSize 1
      startableTestLifecycleAwareForPerSpec.testDescriptions shouldHaveSize 1
   }

   "beforeTestCount for second test should be two" {
      startableTestLifecycleAwareForPerTest.testDescriptions shouldHaveSize 2
      startableTestLifecycleAwareForPerSpec.testDescriptions shouldHaveSize 2
   }
})
