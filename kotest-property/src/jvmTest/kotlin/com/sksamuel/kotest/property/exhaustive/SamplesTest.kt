package com.sksamuel.kotest.property.exhaustive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.samples

@EnabledIf(LinuxCondition::class)
class SamplesTest: StringSpec() {
   init {
      "samples should blow up if list is empty" {
         shouldThrow<IllegalArgumentException> {
            Exhaustive.Companion.samples(listOf<Int>())
         }
      }
      "samples should process list of one element" {
         Exhaustive.Companion.samples(listOf("a")).values shouldContainExactlyInAnyOrder
            listOf(
               listOf("a"),
            )
      }
      "samples should process list of two elements" {
         Exhaustive.Companion.samples(listOf("a", "b")).values shouldContainExactlyInAnyOrder
            listOf(
               listOf("a"),
               listOf("b"),
               listOf("a", "b"),
            )
      }
      "samples should process list of three elements" {
         Exhaustive.Companion.samples(listOf("a", "b", "c")).values shouldContainExactlyInAnyOrder
            listOf(
               listOf("a"),
               listOf("b"),
               listOf("c"),
               listOf("a", "b"),
               listOf("a", "c"),
               listOf("b", "c"),
               listOf("a", "b", "c"),
            )
      }
   }
}
