package com.sksamuel.kotest.property.shrinking

import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.system.captureStandardOut
import io.kotest.matchers.collections.shouldHaveAtMostSize
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.string.shouldContain
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.PropertyTesting
import io.kotest.property.ShrinkingMode
import io.kotest.property.arbitrary.SetShrinker
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.set
import io.kotest.property.checkAll
import io.kotest.property.internal.doShrinking
import io.kotest.property.rtree

@EnabledIf(LinuxCondition::class)
class SetShrinkerTest : FunSpec() {
   init {

      val state = PropertyTesting.shouldPrintShrinkSteps

      beforeSpec {
         PropertyTesting.shouldPrintShrinkSteps = false
      }

      afterSpec {
         PropertyTesting.shouldPrintShrinkSteps = state
      }

      test("SetShrinker should observe range") {
         checkAll(Arb.set(Arb.int(), range = 4..100)) { set ->
            val shrinks = SetShrinker<Int>(4..100).rtree(set)
            val shrunk = doShrinking(shrinks, ShrinkingMode.Unbounded) {
               it shouldHaveSize 0
            }
            shrunk.shrink shouldHaveSize 4
         }
      }

      test("SetShrinker in action") {
         val stdout = captureStandardOut {
            PropertyTesting.shouldPrintShrinkSteps = true
            shouldThrowAny {
               checkAll(PropTestConfig(seed = 123132), Arb.set(Arb.int(0..100))) { set ->
                  set.shouldHaveAtMostSize(3)
               }
            }
         }
         println(stdout)
         stdout.shouldContain("Shrink result (after 24 shrinks) => [1, 100, 96, 29")
      }
   }
}
