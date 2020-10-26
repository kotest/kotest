package io.kotest.engine.listener

import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.spec.toDescription
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

   override fun engineFinished(t: List<Throwable>) {
      listener.engineFinished(t)
   }

   override fun engineStarted(classes: List<KClass<out Spec>>) {
      listener.engineStarted(classes)
   }

   override fun specInstantiated(spec: Spec) {
      if (runningSpec.get() == spec::class.toDescription()) {
         listener.specInstantiated(spec)
      } else {
         queue {
            specInstantiated(spec)
         }
      }
   }

   override fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) {
      if (runningSpec.get() == kclass.toDescription()) {
         listener.specInstantiationError(kclass, t)
      } else {
         queue {
            specInstantiationError(kclass, t)
         }
      }
   }

   override fun testStarted(testCase: TestCase) {
      if (runningSpec.get() == testCase.spec::class.toDescription()) {
         listener.testStarted(testCase)
      } else {
         queue {
            testStarted(testCase)
         }
      }
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      if (runningSpec.get() == testCase.spec::class.toDescription()) {
         listener.testIgnored(testCase, reason)
      } else {
         queue {
            testIgnored(testCase, reason)
         }
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      if (runningSpec.get() == testCase.spec::class.toDescription()) {
         listener.testFinished(testCase, result)
      } else {
         queue {
            testFinished(testCase, result)
         }
      }
   }

   override fun specStarted(kclass: KClass<out Spec>) {
      if (runningSpec.compareAndSet(null, kclass.toDescription())) {
         listener.specStarted(kclass)
      } else {
         queue {
            specStarted(kclass)
         }
      }
   }

   private fun isRunning(kclass: KClass<out Spec>): Boolean {
      val running = runningSpec.get()
      val given = kclass.toDescription()
      return running == given
   }

   override fun specFinished(
      kclass: KClass<out Spec>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      if (runningSpec.get() == kclass.toDescription()) {
         listener.specFinished(kclass, t, results)
         runningSpec.set(null)
         replay()
      } else {
         queue {
            specFinished(kclass, t, results)
         }
      }
   }
}
