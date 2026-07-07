package io.kotest.engine.stable

import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.names.WithDataTestName
import io.kotest.matchers.shouldBe

/**
 * Multiplatform coverage for [StableIdents.getStableIdentifier].
 *
 * These tests run on every target (JVM, JS, Wasm, Native). They guard against the regression
 * where the non-JVM short-circuit (`platform != Platform.JVM -> toString()`) was evaluated
 * before the [WithDataTestName] branch, causing a custom [WithDataTestName.dataTestName] to be
 * silently ignored on all platforms except the JVM.
 */
class StableIdentsCommonTest : FunSpec({

   test("getStableIdentifier should use WithDataTestName.dataTestName() on all platforms") {
      // toString() deliberately differs from dataTestName() so we can tell which one was used.
      val element = object : WithDataTestName {
         override fun dataTestName(): String = "custom-data-test-name"
         override fun toString(): String = "unstable-toString"
      }
      StableIdents.getStableIdentifier(element) shouldBe "custom-data-test-name"
   }

   test("getStableIdentifier should honour WithDataTestName even when toString() is non-deterministic") {
      var counter = 0
      val element = object : WithDataTestName {
         override fun dataTestName(): String = "stable-name"
         override fun toString(): String = "call-${counter++}"
      }
      // Repeated calls must return the same stable identifier regardless of toString() changing.
      StableIdents.getStableIdentifier(element) shouldBe "stable-name"
      StableIdents.getStableIdentifier(element) shouldBe "stable-name"
   }

   test("getStableIdentifier should use toString() for an @IsStableType-annotated type") {
      // @IsStableType signals the type's toString() is stable and may be used as the identifier.
      // On the JVM this is resolved via the IsStableType annotation branch; the resulting identifier
      // (the toString()) is the same on every platform.
      StableIdents.getStableIdentifier(StableTypeForTest("abc")) shouldBe "stable:abc"
      StableIdents.getStableIdentifier(StableTypeForTest("xyz")) shouldBe "stable:xyz"
   }

   test("null should be stable on all platforms") {
      StableIdents.getStableIdentifier(null) shouldBe "<null>"
   }
})

@IsStableType
private data class StableTypeForTest(val value: String) {
   override fun toString(): String = "stable:$value"
}
