package io.kotest.extensions.testcontainers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize

class StartableTestLifecycleAwareTest : StringSpec({
   val startableTestLifecycleAwareForPerTest = StartableTestLifecycleAware()
   val startableTestLifecycleAwareForPerSpec = StartableTestLifecycleAware()

   listeners(startableTestLifecycleAwareForPerTest.perTest(), startableTestLifecycleAwareForPerSpec.perSpec())

   "beforeTestCount for first test should be one" {
      startableTestLifecycleAwareForPerTest.testDescriptions shouldHaveSize 3
      startableTestLifecycleAwareForPerSpec.testDescriptions shouldHaveSize 3
   }

   "beforeTestCount for second test should be two" {
      startableTestLifecycleAwareForPerTest.testDescriptions shouldHaveSize 6
      startableTestLifecycleAwareForPerTest.testDescriptions shouldHaveSize 6
   }
})
