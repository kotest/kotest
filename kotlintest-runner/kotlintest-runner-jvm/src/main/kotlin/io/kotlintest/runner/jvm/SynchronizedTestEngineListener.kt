package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import kotlin.reflect.KClass

class SynchronizedTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    synchronized(listener) {
      listener.engineStarted(classes)
    }
  }

  override fun engineFinished(t: Throwable?) {
    synchronized(listener) {
      listener.engineFinished(t)
    }
  }

  override fun prepareSpec(description: Description, klass: KClass<out Spec>) {
    synchronized(listener) {
      listener.prepareSpec(description, klass)
    }
  }

  override fun completeSpec(description: Description, klass: KClass<out Spec>, t: Throwable?) {
    synchronized(listener) {
      listener.completeSpec(description, klass, t)
    }
  }

  override fun prepareTestCase(testCase: TestCase) {
    synchronized(listener) {
      listener.prepareTestCase(testCase)
    }
  }

  override fun completeTestCase(testCase: TestCase, result: TestResult) {
    synchronized(listener) {
      listener.completeTestCase(testCase, result)
    }
  }

  override fun testRun(set: TestSet, k: Int) {
    synchronized(listener) {
      listener.testRun(set, k)
    }
  }

  override fun completeTestSet(set: TestSet, result: TestResult) {
    synchronized(listener) {
      listener.completeTestSet(set, result)
    }
  }

  override fun specCreated(spec: Spec) {
    synchronized(listener) {
      listener.specCreated(spec)
    }
  }
}