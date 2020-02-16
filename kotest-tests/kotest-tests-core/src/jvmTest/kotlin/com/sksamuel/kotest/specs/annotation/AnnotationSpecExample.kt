package com.sksamuel.kotest.specs.annotation

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

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
