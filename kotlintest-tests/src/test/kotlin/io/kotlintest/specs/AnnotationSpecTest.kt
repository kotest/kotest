package io.kotlintest.specs

import io.kotlintest.shouldBe

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

  override fun interceptSpec(process: () -> Unit) {
    process()
    count shouldBe 2
  }
}