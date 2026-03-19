package com.sksamuel.kotest.engine.spec.isolation

import io.kotest.common.KotestTesting
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.ExpectSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.spec.style.WordSpec
import io.kotest.engine.TestEngineContext
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.spec.SpecInstantiationException
import io.kotest.engine.spec.execution.SpecRefExecutor
import io.kotest.engine.test.TestResult
import io.kotest.matchers.types.shouldBeInstanceOf

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

@OptIn(KotestTesting::class)
@EnabledIf(LinuxOnlyGithubCondition::class)
class InitializerExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : AbstractTestEngineListener() {
      override suspend fun specFinished(ref: SpecRef, result: TestResult) {
         result.errorOrNull?.let { error = it }
      }
   }

   "an exception in the initializer" should {
      "fail the test for behavior spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(BehaviorSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for feature spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(FeatureSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for word spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(WordSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for should spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(ShouldSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for string spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(StringSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for describe spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(DescribeSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for free spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(FreeSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for fun spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(FunSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
      "fail the test for expect spec" {
         val executor = SpecRefExecutor(TestEngineContext(null, listener))
         executor.execute(SpecRef.Reference(ExpectSpecWithInitError::class))
         error.shouldBeInstanceOf<SpecInstantiationException>()
      }
   }
})
