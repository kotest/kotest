package com.sksamuel.kotlintest.specs

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.shouldBe
import io.kotlintest.specs.AnnotationSpec
import io.kotlintest.specs.Test

class AnnotationSpecTest : AnnotationSpec() {

  var count = 0

  @Test
  fun test1() {
    count += 1
  }

  @Test
  fun test2() {
    count += 1
  }

  override fun afterSpec(description: Description, spec: Spec) {
    count shouldBe 2
  }
}