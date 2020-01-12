package io.kotest.runner.jvm

import io.kotest.core.*
import io.kotest.core.spec.SpecConfiguration
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.KClass

@Suppress("LocalVariableName")
class IsolationTestEngineListener(val listener: TestEngineListener) : TestEngineListener {

   private val logger = LoggerFactory.getLogger(this.javaClass)
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

   override fun testStarted(testCase: TestCase) {
      if (runningSpec.get() == testCase.spec::class.description()) {
         listener.testStarted(testCase)
      } else {
         queue {
            testStarted(testCase)
         }
      }
   }

   override fun testIgnored(testCase: TestCase, reason: String?) {
      if (runningSpec.get() == testCase.spec::class.description()) {
         listener.testIgnored(testCase, reason)
      } else {
         queue {
            testIgnored(testCase, reason)
         }
      }
   }

   override fun testFinished(testCase: TestCase, result: TestResult) {
      if (runningSpec.get() == testCase.spec::class.description()) {
         listener.testFinished(testCase, result)
      } else {
         queue {
            testFinished(testCase, result)
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

   override fun specStarted(kclass: KClass<out SpecConfiguration>) {
      if (isRunning(kclass)) {
         listener.specStarted(kclass)
      } else {
         logger.trace("Queuing")
         queue {
            specStarted(kclass)
         }
      }
   }

   private fun isRunning(klass: KClass<out SpecConfiguration>): Boolean {
      val running = runningSpec.get()
      val given = klass.description()
      return running == given
   }

   override fun specFinished(
      klass: KClass<out SpecConfiguration>,
      t: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      if (runningSpec.get() == klass.description()) {
         listener.specFinished(klass, t, results)
         runningSpec.set(null)
         replay()
      } else {
         queue {
            specFinished(klass, t, results)
         }
      }
   }
}
