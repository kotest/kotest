package io.kotest.engine.listener

import io.kotest.common.KotestInternal
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.names.TestName
import io.kotest.core.spec.Spec
import io.kotest.core.spec.SpecRef
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.reflect.KClass

/**
 * A [TestEngineListener] that collects test results and errors into maps so they can be
 * programmatically queried after the test run.
 *
 * This listener is useful for testing purposes, allowing you to assert the results of tests.
 */
@KotestInternal
class CollectingTestEngineListener : AbstractTestEngineListener(), Mutex by Mutex() {

   val specs = mutableMapOf<KClass<*>, TestResult>()
   val tests = mutableMapOf<TestCaseKey, TestResult>()
   val names = mutableListOf<String>()
   var errors = false

   fun result(descriptor: Descriptor.TestDescriptor): TestResult? = tests.mapKeys { it.key.descriptor }[descriptor]
   fun result(testname: String): TestResult? = tests.mapKeys { it.key.name.name }[testname]

   override suspend fun specFinished(ref: SpecRef, result: TestResult) = withLock {
      specs[ref.kclass] = result
      if (result.isErrorOrFailure) errors = true
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) = withLock {
      specs[kclass] = TestResult.Ignored(reason)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?): Unit = withLock {
      tests[testCase.toKey()] = TestResult.Ignored(reason)
      names.add(testCase.name.name)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult): Unit = withLock {
      tests[testCase.toKey()] = result
      if (result.isFailure || result.isError) errors = true
      names.add(testCase.name.name)
   }

   override suspend fun engineFinished(t: List<Throwable>) = withLock {
      if (t.isNotEmpty()) errors = true
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
}

/**
 * A [TestEngineListener] that collects test events into a single list in order of occurrence.
 * This listener is useful for testing purposes, allowing you to assert the order and content of test events.
 */
@KotestInternal
class TestEventsTestEngineListener : AbstractTestEngineListener(), Mutex by Mutex() {

   sealed interface TestEvent {
      data class TestStarted(val name: String) : TestEvent
      data class TestFinished(val name: String, val error: Throwable?) : TestEvent
      data class SpecStarted(val kclass: KClass<*>) : TestEvent
      data class SpecFinished(val kclass: KClass<*>, val error: Throwable?) : TestEvent
      data class SpecIgnored(val kclass: KClass<*>) : TestEvent
      data class TestIgnored(val name: String) : TestEvent
   }

   val events = mutableListOf<TestEvent>()

   override suspend fun specFinished(ref: SpecRef, result: TestResult): Unit = withLock {
      events.add(TestEvent.SpecFinished(ref.kclass, result.errorOrNull))
   }

   override suspend fun specStarted(ref: SpecRef): Unit = withLock {
      events.add(TestEvent.SpecStarted(ref.kclass))
   }

   override suspend fun testStarted(testCase: TestCase): Unit = withLock {
      events.add(TestEvent.TestStarted(testCase.name.name))
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?): Unit = withLock {
      events.add(TestEvent.SpecIgnored(kclass))
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?): Unit = withLock {
      events.add(TestEvent.TestIgnored(testCase.name.name))
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult): Unit = withLock {
      events.add(TestEvent.TestFinished(testCase.name.name, result.errorOrNull))
   }
}
