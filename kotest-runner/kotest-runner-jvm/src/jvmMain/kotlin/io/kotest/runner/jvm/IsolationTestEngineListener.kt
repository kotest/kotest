package io.kotest.runner.jvm

import io.kotest.core.*
import io.kotest.core.spec.SpecConfiguration
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

  override fun engineStarted(classes: List<KClass<out SpecConfiguration>>) {
    listener.engineStarted(classes)
  }

  override fun specCreated(spec: SpecConfiguration) {
    if (runningSpec.compareAndSet(null, spec::class.description())) {
      listener.specCreated(spec)
    } else {
      queue {
        specCreated(spec)
      }
    }
  }

  override fun specInitialisationFailed(klass: KClass<out SpecConfiguration>, t: Throwable) {
    if (runningSpec.compareAndSet(null, klass.description())) {
      listener.specInitialisationFailed(klass, t)
    } else {
      queue {
        specInitialisationFailed(klass, t)
      }
    }
  }

  override fun beforeSpecClass(klass: KClass<out SpecConfiguration>) {
    if (isRunning(klass)) {
      listener.beforeSpecClass(klass)
    } else {
      queue {
        beforeSpecClass(klass)
      }
    }
  }

  private fun isRunning(klass: KClass<out SpecConfiguration>): Boolean {
    val running = runningSpec.get()
    val given = klass.description()
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

  override fun afterSpecClass(klass: KClass<out SpecConfiguration>, t: Throwable?) {
    if (runningSpec.get() == klass.description()) {
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
