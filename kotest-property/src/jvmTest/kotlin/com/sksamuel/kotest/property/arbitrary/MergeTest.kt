package com.sksamuel.kotest.property.arbitrary

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant
import io.kotest.property.arbitrary.merge
import io.kotest.property.arbitrary.take
import kotlin.math.abs

@EnabledIf(LinuxOnlyGithubCondition::class)
class MergeTest : FunSpec() {
   init {
      test("merge should distribute equally") {
         val ints = Arb.constant(0).merge(Arb.constant(1)).take(10000).toList()
         val zeros: Int = ints.count { it == 0 }
         val ones: Int = ints.count { it == 1 }
         val diff: Int = abs(zeros - ones)
         diff.shouldBeLessThan(500)
      }
   }
}
