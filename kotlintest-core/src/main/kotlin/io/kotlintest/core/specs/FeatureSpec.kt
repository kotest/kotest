package io.kotlintest.core.specs

import io.kotlintest.core.AbstractSpec
import io.kotlintest.core.TestCase
import io.kotlintest.core.TestCaseDescriptor

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun feature(name: String, init: FeatureScope.() -> Unit) {
    val descriptor = TestCaseDescriptor("Feature: $name")
    specDescriptor.addDescriptor(descriptor)
    FeatureScope(descriptor).init()
  }

  inner class FeatureScope(private val parentDescriptor: TestCaseDescriptor) {
    fun scenario(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Scenario: $name", this@FeatureSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}