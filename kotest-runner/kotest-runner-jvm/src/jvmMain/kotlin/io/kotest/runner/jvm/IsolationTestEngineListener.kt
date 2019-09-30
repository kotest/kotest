package io.kotest.runner.jvm

import io.kotest.Description
import io.kotest.Spec
import io.kotest.TestCase
import io.kotest.TestResult
import io.kotest.core.fromSpecClass
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

@Suppress("LocalVariableName")
class IsolationTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

  private val runningSpec = AtomicReference<Description?>(null)
  private val callbacks = mutableListOf<() -> Unit>()

  private fun queue(fn: () -> Unit) {
    callbacks.add { fn() }
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

  override fun specInitialisationFailed(klass: KClass<out Spec>, t: Throwable) {
    if (runningSpec.compareAndSet(null, Description.fromSpecClass(klass))) {
      listener.specInitialisationFailed(klass, t)
    } else {
      queue {
        specInitialisationFailed(klass, t)
      }
    }
  }

  override fun beforeSpecClass(klass: KClass<out Spec>) {
    if (isRunning(klass)) {
      listener.beforeSpecClass(klass)
    } else {
      queue {
        beforeSpecClass(klass)
      }
    }
  }

  private fun isRunning(klass: KClass<out Spec>): Boolean {
    val running = runningSpec.get()
    val given = Description.fromSpecClass(klass)
    return running == given
  }

  override fun enterTestCase(testCase: TestCase) {
    if (runningSpec.get() == testCase.spec.description()) {
      listener.enterTestCase(testCase)
    } else {
      queue {
        enterTestCase(testCase)
      }
    }
  }

  override fun invokingTestCase(testCase: TestCase, k: Int) {
    if (runningSpec.get() == testCase.spec.description()) {
      listener.invokingTestCase(testCase, k)
    } else {
      queue {
        invokingTestCase(testCase, k)
      }
    }
  }

  override fun afterTestCaseExecution(testCase: TestCase, result: TestResult) {
    if (runningSpec.get() == testCase.spec.description()) {
      listener.afterTestCaseExecution(testCase, result)
    } else {
      queue {
        afterTestCaseExecution(testCase, result)
      }
    }
  }

  override fun exitTestCase(testCase: TestCase, result: TestResult) {
    if (runningSpec.get() == testCase.spec.description()) {
      listener.exitTestCase(testCase, result)
    } else {
      queue {
        exitTestCase(testCase, result)
      }
    }
  }

  override fun afterSpecClass(klass: KClass<out Spec>, t: Throwable?) {
    if (runningSpec.get() == Description.fromSpecClass(klass)) {
      listener.afterSpecClass(klass, t)
      runningSpec.set(null)
      replay()
    } else {
      queue {
        afterSpecClass(klass, t)
      }
    }
  }
}
