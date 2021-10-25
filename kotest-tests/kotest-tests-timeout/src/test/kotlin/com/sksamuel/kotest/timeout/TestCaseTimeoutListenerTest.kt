package com.sksamuel.kotest.timeout

import io.kotest.core.listeners.TestListener
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration

@DelicateCoroutinesApi
@Suppress("BlockingMethodInNonBlockingContext")
class TestCaseTimeoutListenerTest : FunSpec() {

   private val blockingCount = AtomicInteger(0)
   private val suspendingCount = AtomicInteger(0)

   init {

      blockingTest = true

      afterSpec {
         suspendingCount.get() shouldBe 1
         blockingCount.get() shouldBe 1
      }

      // todo figure this out
      test("tests which timeout during a blocking operation should still run the 'after test' listeners").config(
         timeout = Duration.milliseconds(10000),
         blockingTest = true,
      ) {

         // this listener will flick the flag to true, so we know it ran
         val listener = object : TestListener {
            override suspend fun afterAny(testCase: TestCase, result: TestResult) {
               blockingCount.incrementAndGet()
            }
         }

         // todo
//         val testCase = TestCase.test(
//            TestCaseTimeoutListenerTest::class.toDescriptor().append("wibble"),
//            TestName("wibble"),
//            this@TestCaseTimeoutListenerTest,
//            parent = null,
//         ) {
//            Thread.sleep(1000000)
//         }.copy(
//            config = TestCaseConfig(
//               true,
//               invocations = 1,
//               threads = 1,
//               timeout = Duration.milliseconds(1),
//               listeners = listOf(listener)
//            )
//         )
//
//         val executor = TestCaseExecutor(NoopTestCaseExecutionListener, NoopCoroutineDispatcherFactory, Configuration())
//         // needs to run on a separate thread, so we don't interrupt our own thread
//         withContext(Dispatchers.IO) {
//            executor.execute(testCase, NoopTestContext(testCase, coroutineContext))
//         }
      }

      test("tests which timeout during a suspending operation should still run the 'after test' listeners").config(
         timeout = Duration.milliseconds(10000)
      ) {

         // this listener will flick the flag to true, so we know it ran
         val listener = object : TestListener {
            override suspend fun afterAny(testCase: TestCase, result: TestResult) {
               suspendingCount.incrementAndGet()
            }
         }

         // todo
//         val testCase = TestCase(
//            descriptor = TestCaseTimeoutListenerTest::class.toDescriptor().append("wibble"),
//            TestName("wibble"),
//            this@TestCaseTimeoutListenerTest,
//            parent = null,
//            config = TestCaseConfig(
//               true,
//               invocations = 1,
//               threads = 1,
//               timeout = Duration.milliseconds(1),
//               listeners = listOf(listener)
//            ),
//               test = {
//               delay(1000000)
//            }
//         )
//
//         val executor = TestCaseExecutor(NoopTestCaseExecutionListener, NoopCoroutineDispatcherFactory, Configuration())
//         // needs to run on a separate thread, so we don't interrupt our own thread
//         withContext(Dispatchers.IO) {
//            executor.execute(testCase, NoopTestContext(testCase, coroutineContext))
//         }
      }
   }
}
