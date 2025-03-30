package com.sksamuel.kotest.submatching

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.submatching.topNWithTiesBy

@EnabledIf(LinuxOnlyGithubCondition::class)
class TopNWithTiesByTest : StringSpec() {
   init {
      "work without ties" {
         listOf("apple", "banana", "pear").topNWithTiesBy(depth = 2) { it.length } shouldBe listOf("banana", "apple")
      }
      "work with ties" {
         listOf("apple", "pea", "banana", "orange", "lemon", "pear")
            .topNWithTiesBy(depth = 2) { it.length } shouldBe
            listOf("banana", "orange", "apple", "lemon")
      }
   }
}
