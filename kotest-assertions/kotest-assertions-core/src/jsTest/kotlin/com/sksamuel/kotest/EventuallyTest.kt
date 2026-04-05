@file:Suppress("RUNTIME_ANNOTATION_NOT_SUPPORTED")

package com.sksamuel.kotest

import io.kotest.assertions.nondeterministic.eventually
import io.kotest.core.annotation.Issue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.seconds

@Issue("https://github.com/kotest/kotest/issues/5838")
class EventuallyTest : FunSpec() {
   init {
      test("eventually listener default should work on js platforms") {
         var a = 0
         eventually(1.seconds) {
            a++
            a shouldBe 5
         }
      }
   }
}
