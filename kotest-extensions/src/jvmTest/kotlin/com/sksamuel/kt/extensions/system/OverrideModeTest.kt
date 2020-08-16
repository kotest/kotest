package com.sksamuel.kt.extensions.system

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.extensions.system.OverrideMode
import io.kotest.extensions.system.OverrideMode.SetOrError
import io.kotest.extensions.system.OverrideMode.SetOrError.IllegalOverrideException
import io.kotest.extensions.system.OverrideMode.SetOrIgnore
import io.kotest.extensions.system.OverrideMode.SetOrOverride
import io.kotest.inspectors.forAll
import io.kotest.matchers.shouldBe

class OverrideModeTest : WordSpec() {

   private val originalMap = mapOf("foo" to "bar", "boot" to "baat")

   init {

      "SetOrOverride" should {
         val mode = SetOrOverride

         "Set value if not already set" {
            mode.shouldSetValueIfNotAlreadySet()
         }

         "Set value to null if override is null" {
            mode.overrideOriginalWithNullFoo() shouldBe mapOf("boot" to "baat")
         }

         "Override any present values with new ones" {
            mode.overrideOriginalWithNewValue().values.forAll { it shouldBe "New Value" }
         }

      }

      "SetOrIgnore" should {
         val mode = SetOrIgnore

         "Set values if no already set" {
            mode.shouldSetValueIfNotAlreadySet()
         }

         "Not set value to null if override is null" {
            mode.overrideOriginalWithNullFoo() shouldBe originalMap
         }

         "Not override any present values with new ones" {
            mode.overrideOriginalWithNewValue() shouldBe originalMap
         }

      }

      "SetOrError" should {
         val mode = SetOrError

         "Set values if not already set" {
            mode.shouldSetValueIfNotAlreadySet()
         }

         "Error when attempted to override value to null" {
            shouldThrow<IllegalOverrideException> {
               mode.overrideOriginalWithNullFoo()
            }.message shouldBe "Overriding a variable when mode is set to SetOrError. Use another OverrideMode to allow this. Trying to set {foo=null}"
         }

         "Error when attempted to override any present value with new ones" {
            shouldThrow<IllegalOverrideException> {
               mode.overrideOriginalWithNewValue()
            }.message shouldBe "Overriding a variable when mode is set to SetOrError. Use another OverrideMode to allow this. Trying to set {foo=New Value, boot=New Value}"
         }
      }
   }

   private fun OverrideMode.shouldSetValueIfNotAlreadySet() {
      val unset = mapOf("AbsentKey" to "New Value")
      override(originalMap, unset) shouldBe originalMap + unset
   }

   private fun OverrideMode.overrideOriginalWithNullFoo(): Map<String, String?> {
      val setToNull: Map<String, String?> = mapOf("foo" to null)
      return override(originalMap, setToNull)
   }

   private fun OverrideMode.overrideOriginalWithNewValue(): Map<String, String?> {
      val newMap = originalMap.mapValues { "New Value" }
      return override(originalMap, newMap)
   }
}
