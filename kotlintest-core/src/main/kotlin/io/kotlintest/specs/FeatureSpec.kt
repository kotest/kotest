package io.kotlintest.specs

import io.kotlintest.AbstractSpec
import io.kotlintest.TestCase
import io.kotlintest.TestContainer

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : AbstractSpec() {

  init {
    body()
  }

  fun feature(name: String, init: FeatureScope.() -> Unit) {
    val descriptor = TestContainer("Feature: $name")
    rootContainer.addContainer(descriptor)
    FeatureScope(descriptor).init()
  }

  inner class FeatureScope(private val parentDescriptor: TestContainer) {
    fun scenario(name: String, test: () -> Unit): TestCase {
      val tc = TestCase("Scenario: $name", nextId(), this@FeatureSpec, parentDescriptor, test, defaultTestCaseConfig)
      parentDescriptor.addTest(tc)
      return tc
    }
  }
}