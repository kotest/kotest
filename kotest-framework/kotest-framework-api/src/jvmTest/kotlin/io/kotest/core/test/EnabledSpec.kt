package io.kotest.core.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import java.util.UUID

class EnabledSpec : FunSpec({
   test("multiple enabled should resolve to enabled") {
      val result = (0..3).map { Enabled.enabled }.let { Enabled.fold(it) }
      result.isEnabled shouldBe true
      result.reason.shouldBeEmpty()
   }

   test("multiple enabled and a single disabled should resolve to disabled") {
      val reason = UUID.randomUUID().toString()
      val result = ((0..3).map { Enabled.enabled } + Enabled.disabled(reason)).let { Enabled.fold(it.shuffled()) }
      result.isEnabled shouldBe false
      result.reason shouldBe reason
   }

   test("multiple disabled should combine reasons when resolved") {
      val actives = (0..10).map { listOf(Enabled.disabled(UUID.randomUUID().toString()), Enabled.enabled) }.flatten()
      val result = Enabled.fold(actives)
      val expected = StringBuilder().apply {
         actives.filter { !it.isEnabled }.forEach { appendLine(it.reason) }
      }.trim()

      result.isEnabled shouldBe false
      result.reason shouldBe expected.toString()
   }
})
