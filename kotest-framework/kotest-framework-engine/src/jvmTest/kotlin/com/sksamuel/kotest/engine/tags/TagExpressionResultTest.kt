package com.sksamuel.kotest.engine.tags

import io.kotest.core.NamedTag
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.StringSpec
import io.kotest.engine.tags.Expression
import io.kotest.engine.tags.TagExpressionResult
import io.kotest.engine.tags.isPotentiallyActive
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class TagExpressionResultTest : StringSpec() {
   init {

      // For OR, Include is absorbing and Exclude is the identity (the other branch decides).
      "or lattice: Include is absorbing" {
         (TagExpressionResult.Include or TagExpressionResult.Include) shouldBe TagExpressionResult.Include
         (TagExpressionResult.Include or TagExpressionResult.Exclude) shouldBe TagExpressionResult.Include
         (TagExpressionResult.Include or TagExpressionResult.Inconclusive) shouldBe TagExpressionResult.Include
         (TagExpressionResult.Exclude or TagExpressionResult.Include) shouldBe TagExpressionResult.Include
         (TagExpressionResult.Inconclusive or TagExpressionResult.Include) shouldBe TagExpressionResult.Include
      }

      "or lattice: Exclude is the identity" {
         (TagExpressionResult.Exclude or TagExpressionResult.Exclude) shouldBe TagExpressionResult.Exclude
         (TagExpressionResult.Exclude or TagExpressionResult.Inconclusive) shouldBe TagExpressionResult.Inconclusive
         (TagExpressionResult.Inconclusive or TagExpressionResult.Exclude) shouldBe TagExpressionResult.Inconclusive
         (TagExpressionResult.Inconclusive or TagExpressionResult.Inconclusive) shouldBe TagExpressionResult.Inconclusive
      }

      // For AND, Exclude is absorbing and Include is the identity.
      "and lattice: Exclude is absorbing" {
         (TagExpressionResult.Include and TagExpressionResult.Include) shouldBe TagExpressionResult.Include
         (TagExpressionResult.Include and TagExpressionResult.Exclude) shouldBe TagExpressionResult.Exclude
         (TagExpressionResult.Include and TagExpressionResult.Inconclusive) shouldBe TagExpressionResult.Inconclusive
         (TagExpressionResult.Exclude and TagExpressionResult.Include) shouldBe TagExpressionResult.Exclude
         (TagExpressionResult.Inconclusive and TagExpressionResult.Exclude) shouldBe TagExpressionResult.Exclude
         (TagExpressionResult.Inconclusive and TagExpressionResult.Include) shouldBe TagExpressionResult.Inconclusive
         (TagExpressionResult.Inconclusive and TagExpressionResult.Inconclusive) shouldBe TagExpressionResult.Inconclusive
      }

      // '!linux | windows' on a linux-tagged spec must remain potentially active:
      // the spec is negated by the left branch, but an inner test could carry the 'windows' tag.
      "spec-negated OR branch should not eagerly exclude when the other branch is inconclusive" {
         val expr = Expression.Or(
            Expression.Not(Expression.Identifier("linux")),
            Expression.Identifier("windows"),
         )
         expr.isPotentiallyActive(setOf(NamedTag("linux"))) shouldBe TagExpressionResult.Inconclusive
      }

      "OR where both branches exclude the spec should be excluded" {
         val expr = Expression.Or(
            Expression.Not(Expression.Identifier("linux")),
            Expression.Not(Expression.Identifier("linux")),
         )
         expr.isPotentiallyActive(setOf(NamedTag("linux"))) shouldBe TagExpressionResult.Exclude
      }
   }
}
