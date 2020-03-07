package io.kotest.core.engine

import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlin.reflect.KClass

class SynchronizedTestEngineListener(private val listener: TestEngineListener) : TestEngineListener {

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    synchronized(listener) {
      super.engineStarted(classes)
    }
  }

  override fun engineFinished(t: Throwable?) {
    synchronized(listener) {
      super.engineFinished(t)
    }
  }

  override fun specStarted(kclass: KClass<out Spec>) {
    synchronized(listener) {
      super.specStarted(kclass)
    }
  }

  override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
    synchronized(listener) {
      super.specFinished(kclass, t, results)
    }
  }

  override fun testStarted(testCase: TestCase) {
    synchronized(listener) {
      super.testStarted(testCase)
    }
  }

  override fun testIgnored(testCase: TestCase, reason: String?) {
    synchronized(listener) {
      super.testIgnored(testCase, reason)
    }
  }

  override fun testFinished(testCase: TestCase, result: TestResult) {
    synchronized(listener) {
      super.testFinished(testCase, result)
    }
  }

  override fun specInstantiated(spec: Spec) {
    synchronized(listener) {
      super.specInstantiated(spec)
    }
  }

  override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
    synchronized(listener) {
      super.specInstantiationError(kclass, t)
    }
  }
}
