package com.sksamuel.kotlintest.specs.annotation

import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec

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