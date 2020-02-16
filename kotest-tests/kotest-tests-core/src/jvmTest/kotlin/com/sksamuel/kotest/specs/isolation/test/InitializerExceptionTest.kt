package com.sksamuel.kotest.specs.isolation.test

import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.*
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.core.engine.TestEngineListener
import io.kotest.core.engine.SpecExecutor
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass

internal class BehaviorSpecWithInitError : BehaviorSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class FunSpecWithInitError : FunSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class StringSpecWithInitError : StringSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class ShouldSpecWithInitError : ShouldSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class DescribeSpecWithInitError : DescribeSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class FeatureSpecWithInitError : FeatureSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class ExpectSpecWithInitError : ExpectSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class FreeSpecWithInitError : FreeSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

internal class WordSpecWithInitError : WordSpec() {
   override fun isolationMode() = IsolationMode.InstancePerTest

   init {
      error("boom")
   }
}

class InitializerExceptionTest : WordSpec({

   var error: Throwable? = null

   val listener = object : TestEngineListener {
      override fun specFinished(kclass: KClass<out Spec>, t: Throwable?, results: Map<TestCase, TestResult>) {
         if (t != null) error = t
      }
   }

   "an exception in the initializer" should {
      "fail the test for behavior spec" {
         val executor = SpecExecutor(listener)
         executor.execute(BehaviorSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for feature spec" {
         val executor = SpecExecutor(listener)
         executor.execute(FeatureSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for word spec" {
         val executor = SpecExecutor(listener)
         executor.execute(WordSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for should spec" {
         val executor = SpecExecutor(listener)
         executor.execute(ShouldSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for string spec" {
         val executor = SpecExecutor(listener)
         executor.execute(StringSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for describe spec" {
         val executor = SpecExecutor(listener)
         executor.execute(DescribeSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for free spec" {
         val executor = SpecExecutor(listener)
         executor.execute(FreeSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for fun spec" {
         val executor = SpecExecutor(listener)
         executor.execute(FunSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
      "fail the test for expect spec" {
         val executor = SpecExecutor(listener)
         executor.execute(ExpectSpecWithInitError::class)
         error.shouldBeInstanceOf<InvocationTargetException>()
      }
   }
})
