package io.kotest.core.test

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeEmpty
import java.util.UUID

class IsActiveSpec : FunSpec({
   test("multiple active should resolve to active") {
      val result = (0..3).map { IsActive.active }.let { IsActive.fold(it) }
      result.active shouldBe true
      result.reason.shouldBeEmpty()
   }

   test("multiple active and a single inactive should resolve to inactive") {
      val reason = UUID.randomUUID().toString()
      val result = ((0..3).map { IsActive.active } + IsActive.inactive(reason)).let { IsActive.fold(it.shuffled()) }
      result.active shouldBe false
      result.reason shouldBe reason
   }

   test("multiple inactive should combine reasons when resolved") {
      val actives = (0..10).map { listOf(IsActive.inactive(UUID.randomUUID().toString()), IsActive.active) }.flatten()
      val result = IsActive.fold(actives)
      val expected = StringBuilder().apply {
         actives.filter { !it.active }.forEach { appendLine(it.reason) }
      }.trim()

      result.active shouldBe false
      result.reason shouldBe expected.toString()
   }
})
