package io.kotlintest.runner.jvm

import io.kotlintest.Description
import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

class IsolationTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

  private val runningSpec = AtomicReference<Description?>(null)
  private val callbacks = mutableListOf<() -> Unit>()

  private fun queue(fn: () -> Unit) {
    callbacks.add({ fn() })
  }

  private fun replay() {
    val _callbacks = callbacks.toList()
    callbacks.clear()
    _callbacks.forEach { it.invoke() }
  }

  override fun engineFinished(t: Throwable?) {
    listener.engineFinished(t)
  }

  override fun engineStarted(classes: List<KClass<out Spec>>) {
    listener.engineStarted(classes)
  }

  override fun specCreated(spec: Spec) {
    if (runningSpec.compareAndSet(null, spec.description())) {
      listener.specCreated(spec)
    } else {
      queue {
        specCreated(spec)
      }
    }
  }

  override fun prepareSpec(description: Description, klass: KClass<out Spec>) {
    if (runningSpec.get() == description) {
      listener.prepareSpec(description, klass)
    } else {
      queue {
        prepareSpec(description, klass)
      }
    }
  }

  override fun prepareTestCase(testCase: TestCase) {
    if (runningSpec.get() == testCase.spec.description()) {
      listener.prepareTestCase(testCase)
    } else {
      queue {
        prepareTestCase(testCase)
      }
    }
  }

  override fun testRun(set: TestSet, k: Int) {
    if (runningSpec.get() == set.testCase.spec.description()) {
      listener.testRun(set, k)
    } else {
      queue {
        testRun(set, k)
      }
    }
  }

  override fun completeTestSet(set: TestSet, result: TestResult) {
    if (runningSpec.get() == set.testCase.spec.description()) {
      listener.completeTestSet(set, result)
    } else {
      queue {
        completeTestSet(set, result)
      }
    }
  }

  override fun completeTestCase(testCase: TestCase, result: TestResult) {
    if (runningSpec.get() == testCase.spec.description()) {
      listener.completeTestCase(testCase, result)
    } else {
      queue {
        completeTestCase(testCase, result)
      }
    }
  }

  override fun completeSpec(description: Description, klass: KClass<out Spec>, t: Throwable?) {
    if (runningSpec.get() == description) {
      listener.completeSpec(description, klass, t)
      runningSpec.set(null)
      replay()
    } else {
      queue {
        completeSpec(description, klass, t)
      }
    }
  }
}