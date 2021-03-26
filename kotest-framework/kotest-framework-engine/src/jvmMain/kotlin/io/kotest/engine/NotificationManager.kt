package io.kotest.engine

import io.kotest.core.config.configuration
import io.kotest.core.config.specInstantiationListeners
import io.kotest.core.config.testListeners
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.engine.listener.TestEngineListener
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlin.reflect.KClass

/**
 * Used to send notifications to listeners and test engine listeners.
 */
class NotificationManager(private val listener: TestEngineListener) {

   /**
    * Notifies listeners that we are about to start execution of a [Descriptor].
    */
   suspend fun specStarted(spec: Descriptor.SpecDescriptor) = Try {
      log("NotificationManager:specStarted $spec")
      listener.specStarted(spec)

      // todo call a new prepare spec listener interface
   }

   suspend fun specFinished(
      spec: Descriptor.SpecDescriptor,
      error: Throwable?,
      results: Map<Descriptor.TestDescriptor, TestResult>
   ) = Try {
      log("NotificationManager:specFinished $spec")
      listener.specFinished(spec, error, results)

      // todo call a new finalize spec listener interface
   }

   /**
    * Notifies listeners that we are about to start execution of a [Spec].
    *
    * This is called only once per spec regardless of the number of instantiation events.
    */
   suspend fun specStarted(kclass: KClass<out Spec>) = Try {
      log("NotificationManager:specStarted $kclass")

      listener.specStarted(kclass)

      // prepareSpec can only be registered at the project level
      // It makes no sense to call prepareSpec after a spec has already been instantiated.
      // Therefore we only look for listeners at the global level only
      val listeners = configuration.testListeners()
      log("Notifying ${listeners.size} listeners of callback 'prepareSpec'")
      listeners.forEach {
         it.prepareSpec(kclass)
      }
      log("'prepareSpec' callbacks complete")
   }

   /**
    * Notifies listners that we have finished the execution of a [Spec].
    * This is called once per spec regardless of the number of instantiation events.
    */
   suspend fun specFinished(
      kclass: KClass<out Spec>,
      error: Throwable?,
      results: Map<TestCase, TestResult>
   ) {
      log("NotificationManager:specFinished $kclass $error")
      userLevelSpecFinished(kclass, results).fold(
         { testEngineSpecFinished(kclass, error ?: it, results) },
         { testEngineSpecFinished(kclass, error, results) }
      )
   }

   suspend fun specSkipped(
      spec: Spec,
      results: Map<TestCase, TestResult>
   ) = Try {
      configuration.testListeners().forEach {
         it.skipSpec(spec, results)
      }
   }

   private fun testEngineSpecFinished(
      kclass: KClass<out Spec>,
      error: Throwable?,
      results: Map<TestCase, TestResult>
   ) = Try {
      error?.printStackTrace()
      listener.specFinished(kclass, error, results)
   }

   // finalize spec's can be registered at the project level or using the dsl
   // dsl callbacks are just project level listeners with a spec class check
   private suspend fun userLevelSpecFinished(
      kclass: KClass<out Spec>,
      results: Map<TestCase, TestResult>
   ) = Try {
      configuration.testListeners().forEach {
         it.finalizeSpec(kclass, results)
      }
   }

   fun specInstantiated(spec: Spec) = Try {
      log("NotificationManager:specInstantiated spec:$spec")
      val listeners = configuration.specInstantiationListeners()
      listener.specInstantiated(spec)
      listeners.forEach {
         it.specInstantiated(spec)
      }
   }

   fun specInstantiationError(kclass: KClass<out Spec>, t: Throwable) = Try {
      log("NotificationManager:specInstantiationError $kclass error:$t")
      val listeners = configuration.specInstantiationListeners()
      t.printStackTrace()
      listener.specInstantiationError(kclass, t)
      listeners.forEach {
         it.specInstantiationError(kclass, t)
      }
   }
}
