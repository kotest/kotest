package io.kotest.engine.listener

import io.kotest.core.descriptors.Descriptor
import io.kotest.core.names.TestName
import io.kotest.core.spec.Spec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

class CollectingTestEngineListener : AbstractTestEngineListener(), Mutex by Mutex() {

   val specs = mutableMapOf<KClass<*>, TestResult>()
   val tests = mutableMapOf<TestCaseKey, TestResult>()
   val names = mutableListOf<String>()
   var errors = false

   /**
    * Counts the number of times [engineStarted] and [engineFinished] have been invoked.
    *
    * Used by [waitForEnginesFinished], so that internal Kotest tests can wait until engines
    * are finished before performing assertions.
    */
   private val activeEngineCount = MutableStateFlow<Int?>(null)

   fun result(descriptor: Descriptor.TestDescriptor): TestResult? = tests.mapKeys { it.key.descriptor }[descriptor]
   fun result(testname: String): TestResult? = tests.mapKeys { it.key.name.testName }[testname]

   override suspend fun specFinished(kclass: KClass<*>, result: TestResult): Unit = withLock {
      specs[kclass] = result
      if (result.isErrorOrFailure) errors = true
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?): Unit = withLock {
      specs[kclass] = TestResult.Ignored(reason)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?): Unit = withLock {
      tests[testCase.toKey()] = TestResult.Ignored(reason)
      names += testCase.name.testName
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult): Unit = withLock {
      tests[testCase.toKey()] = result
      if (result.isFailure || result.isError) errors = true
      names += testCase.name.testName
   }

   override suspend fun engineStarted() {
      activeEngineCount.update { (it ?: 0) + 1 }
   }

   override suspend fun engineFinished(t: List<Throwable>): Unit = withLock {
      if (t.isNotEmpty()) errors = true
      activeEngineCount.update { (it ?: 0) - 1 }
   }

   /**
    * The purpose of this class is to reduce the footprint of the data collected and retained about each test
    * through the whole test suite.
    */
   data class TestCaseKey(
      val descriptor: Descriptor.TestDescriptor,
      val name: TestName,
      val specClass: KClass<out Spec>,
   )

   fun TestCase.toKey(): TestCaseKey {
      return TestCaseKey(this.descriptor, this.name, this.spec::class)
   }

   /** Suspends until at least one engine has started, and all engines have finished. */
   internal suspend fun waitForEnginesFinished() {
      // wait until `engineFinished()` has been invoked the same number of times as `engineStarted()`
      activeEngineCount.first { it == 0 }
   }
}
