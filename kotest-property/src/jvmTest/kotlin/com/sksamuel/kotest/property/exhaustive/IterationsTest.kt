package com.sksamuel.kotest.property.exhaustive

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive

@EnabledIf(LinuxOnlyGithubCondition::class)
class IterationsTest : ShouldSpec({

   should("calculate min iterations to be at least the minimum for an exhaustive") {
      val items = List(10_000) { it }

      exhaustive(items).checkAll {
         // If all of these execute, the exhaustive could infer the minimum iterations
      }
   }
})
