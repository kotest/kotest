package io.kotlintest.specs

import io.kotlintest.ContainerTestDescriptor
import io.kotlintest.Spec
import io.kotlintest.TestCaseDescriptor
import org.junit.platform.engine.TestDescriptor

abstract class FeatureSpec(body: FeatureSpec.() -> Unit = {}) : Spec() {

  init {
    body()
  }

  fun feature(name: String, init: FeatureScope.() -> Unit) {
    val descriptor = ContainerTestDescriptor(specDescriptor.uniqueId.append("container", name), "Feature: " + this)
    specDescriptor.addChild(descriptor)
    FeatureScope(descriptor).init()
  }

  inner class FeatureScope(private val parentDescriptor: TestDescriptor) {
    fun scenario(name: String, test: () -> Unit): TestCaseDescriptor {
      val descriptor = TestCaseDescriptor(parentDescriptor.uniqueId.append("test", name), "Scenario: " + this, source, this@FeatureSpec, test, defaultTestCaseConfig)
      parentDescriptor.addChild(descriptor)
      return descriptor
    }
  }
}