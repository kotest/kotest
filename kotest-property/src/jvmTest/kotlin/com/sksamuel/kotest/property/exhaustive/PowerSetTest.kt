package com.sksamuel.kotest.property.exhaustive

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.property.Exhaustive
import io.kotest.property.exhaustive.powerSet

@EnabledIf(LinuxCondition::class)
class PowerSetTest: StringSpec() {
   init {
      "powerSet should blow up if list is empty" {
         shouldThrow<IllegalArgumentException> {
            Exhaustive.Companion.powerSet(listOf<Int>())
         }
      }
      "powerSet should process list of one element" {
         Exhaustive.Companion.powerSet(listOf("a")).values shouldContainExactlyInAnyOrder
            listOf(
               listOf("a"),
            )
      }
      "powerSet should process list of two elements" {
         Exhaustive.Companion.powerSet(listOf("a", "b")).values shouldContainExactlyInAnyOrder
            listOf(
               listOf("a"),
               listOf("b"),
               listOf("a", "b"),
            )
      }
      "powerSet should process list of three elements" {
         Exhaustive.Companion.powerSet(listOf("a", "b", "c")).values shouldContainExactlyInAnyOrder
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
