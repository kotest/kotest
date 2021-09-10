package io.kotest.property

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import kotlin.native.concurrent.isFrozen
import kotlin.test.Test

class ConfigTests {
   @Test
   fun `The PropertyTesting object is frozen`() {
      PropertyTesting.isFrozen shouldBe true
   }

   @Test
   fun `The frozen PropertyTesting object's properties can be modified without exception`() {
         shouldNotThrow<kotlin.native.concurrent.InvalidMutabilityException> {
            PropertyTesting.maxFilterAttempts = PropertyTesting.maxFilterAttempts
            PropertyTesting.shouldPrintShrinkSteps = PropertyTesting.shouldPrintShrinkSteps
            PropertyTesting.shouldPrintGeneratedValues = PropertyTesting.shouldPrintGeneratedValues
            PropertyTesting.edgecasesBindDeterminism = PropertyTesting.edgecasesBindDeterminism
            PropertyTesting.defaultSeed = PropertyTesting.defaultSeed
            PropertyTesting.defaultMinSuccess = PropertyTesting.defaultMinSuccess
            PropertyTesting.defaultMaxFailure = PropertyTesting.defaultMaxFailure
            PropertyTesting.defaultIterationCount = PropertyTesting.defaultIterationCount
            PropertyTesting.defaultShrinkingMode = PropertyTesting.defaultShrinkingMode
            PropertyTesting.defaultListeners = PropertyTesting.defaultListeners
            PropertyTesting.defaultEdgecasesGenerationProbability = PropertyTesting.defaultEdgecasesGenerationProbability
            PropertyTesting.defaultOutputClassifications = PropertyTesting.defaultOutputClassifications

            true
         }
   }
}

