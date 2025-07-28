package com.sksamuel.kotest.submatching

import io.kotest.assertions.submatching.topNWithTiesBy
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

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
