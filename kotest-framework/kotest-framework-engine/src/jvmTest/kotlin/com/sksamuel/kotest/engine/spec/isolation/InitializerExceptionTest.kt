package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.core.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.core.test.TestResult
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.SpecExecutor
import io.kotest.engine.spec.SpecInstantiationException
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlin.reflect.KClass

private class BehaviorSpecWithInitError : BehaviorSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class FunSpecWithInitError : FunSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class StringSpecWithInitError : StringSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class ShouldSpecWithInitError : ShouldSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class DescribeSpecWithInitError : DescribeSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class FeatureSpecWithInitError : FeatureSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class ExpectSpecWithInitError : ExpectSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class FreeSpecWithInitError : FreeSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

private class WordSpecWithInitError : WordSpec() {
   override fun isolationMode() = IsolationMode.InstancePerRoot

   init {
      error("boom")
   }
}

@EnabledIf(LinuxOnlyGithubCondition::class)
class InitializerExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : AbstractTestEngineListener() {
      override suspend fun specFinished(kclass: KClass<*>, result: TestResult) {
         result.errorOrNull?.let { error = it }
      }
   }

   "an exception in the initializer" should {
      "fail the test for behavior spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(BehaviorSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for feature spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(FeatureSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for word spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(WordSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for should spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(ShouldSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for string spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(StringSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for describe spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(DescribeSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for free spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(FreeSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for fun spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(FunSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for expect spec" {
         val executor = SpecExecutor(EngineContext(null, Platform.JVM).withListener(listener))
         executor.execute(ExpectSpecWithInitError::class)
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
   }
})
