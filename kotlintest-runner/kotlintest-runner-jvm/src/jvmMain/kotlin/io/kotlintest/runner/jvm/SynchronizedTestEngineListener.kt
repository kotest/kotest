package io.kotlintest.runner.jvm

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

  override fun beforeSpecClass(klass: KClass<out Spec>) {
    synchronized(listener) {
      listener.beforeSpecClass(klass)
    }
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {
    synchronized(listener) {
      listener.afterSpecClass(klass, t)
    }
  }

  override fun enterTestCase(testCase: TestCase) {
    synchronized(listener) {
      listener.enterTestCase(testCase)
    }
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    synchronized(listener) {
      listener.exitTestCase(testCase, result)
    }
  }

  override fun invokingTestCase(testCase: TestCase, k: Int) {
    synchronized(listener) {
      listener.invokingTestCase(testCase, k)
    }
  }

  override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
    synchronized(listener) {
      listener.afterTestCaseExecution(testCase, result)
    }
  }

  override fun specCreated(spec: Spec) {
    synchronized(listener) {
      listener.specCreated(spec)
    }
  }
}