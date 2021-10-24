package com.sksamuel.kotest.engine

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.filter.TestFilter
import io.kotest.core.filter.TestFilterResult
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class TestFilterTest : StringSpec() {
  init {
     // this test will be ignored through the test case filter that we have registered in project config
     "bb should be ignored" {
        1 shouldBe 2
     }
  }
}

object TestFilterTestFilter : TestFilter {
  override fun filter(descriptor: Descriptor): TestFilterResult {
    return when (descriptor.id.value) {
      "bb should be ignored" -> TestFilterResult.Exclude(null)
      else -> TestFilterResult.Include
    }
  }
}
