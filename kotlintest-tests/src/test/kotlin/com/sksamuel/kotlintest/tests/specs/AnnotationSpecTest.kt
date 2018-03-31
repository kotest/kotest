package com.sksamuel.kotlintest.tests.specs

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

  override fun interceptSpec(spec: Spec, process: () -> Unit) {
    process()
    count shouldBe 2
  }
}