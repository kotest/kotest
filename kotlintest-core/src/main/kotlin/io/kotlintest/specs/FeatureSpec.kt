package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestScope

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun feature(name: String, init: FeatureScope.() -> Unit) {
    val descriptor = TestScope("Feature: $name", this@FeatureSpec)
    rootContainer.addContainer(descriptor)
    FeatureScope(descriptor).init()
  }

  inner class FeatureScope(private val parentDescriptor: TestScope) {
    fun scenario(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Scenario: $name", this@FeatureSpec, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}