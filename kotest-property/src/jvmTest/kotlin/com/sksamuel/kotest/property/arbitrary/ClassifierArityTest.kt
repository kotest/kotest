package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.string.shouldContain
import io.kotest.property.PropTestConfig
import io.kotest.property.checkAll

@EnabledIf(LinuxCondition::class)
class ClassifierArityTest : FunSpec() {
   init {

      test("classifiers for prop test arity 2") {
         val out = captureStandardOut {
            checkAll<String, Int>(PropTestConfig(outputClassifications = true, seed = 678673131234)) { _, _ -> }
         }
         repeat(2) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 3") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _ -> }
         }
         repeat(3) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 4") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _ -> }
         }
         repeat(4) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 5") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _ -> }
         }
         repeat(5) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 6") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _ -> }
         }
         repeat(6) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 7") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _, _ -> }
         }
         repeat(7) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 8") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _, _, _ -> }
         }
         repeat(8) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 9") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _, _, _, _ -> }
         }
         repeat(9) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 10") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _, _, _, _, _ -> }
         }
         repeat(10) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 11") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _, _, _, _, _, _ -> }
         }
         repeat(11) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }

      test("classifiers for prop test arity 12") {
         val out = captureStandardOut {
            checkAll<Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int>(
               PropTestConfig(
                  outputClassifications = true,
                  seed = 678673131234
               )
            ) { _, _, _, _, _, _, _, _, _, _, _, _ -> }
         }
         repeat(12) { k ->
            out.shouldContain("Label statistics for arg $k (1000 inputs):")
         }
      }
   }
}
