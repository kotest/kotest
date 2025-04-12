package com.sksamuel.kotest.engine.spec.annotation

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class AnnotationSpecExample : AnnotationSpec() {

   @Test
   fun test1() {
      1 shouldBe 1
   }

   @Test
   fun test2() {
      3 shouldBe 3
   }
}
