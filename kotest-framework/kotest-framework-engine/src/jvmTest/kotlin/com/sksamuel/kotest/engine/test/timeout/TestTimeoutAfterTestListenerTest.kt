package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.Platform
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.names.TestName
import io.kotest.core.source.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.test.NoopTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

@DelicateCoroutinesApi
@Suppress("BlockingMethodInNonBlockingContext")
class TestTimeoutAfterTestListenerTest : FunSpec() {
   init {

      test("tests that timeout during a blocking operation should still run the 'after test' listeners").config(
         timeout = 10000.milliseconds,
         blockingTest = true,
      ) {

         val blockingCount = AtomicInteger(0)

         // this listener will flick the flag to true, so we know it ran
         val listener = object : AfterTestListener {
            override suspend fun afterTest(testCase: TestCase, result: TestResult) {
               blockingCount.incrementAndGet()
            }
         }

         val tc = TestCase(
            descriptor = TestTimeoutAfterTestListenerTest::class.toDescriptor().append("wibble"),
            name = TestName("wibble"),
            spec = this@TestTimeoutAfterTestListenerTest,
            test = { Thread.sleep(1000000) },
            source = sourceRef(),
            type = TestType.Container,
            parent = null,
            config = ResolvedTestConfig.default.copy(
               timeout = 1.milliseconds,
               extensions = listOf(listener),
               blockingTest = true
            ),
         )

         val executor =
            TestCaseExecutor(
               NoopTestCaseExecutionListener,
               NoopCoroutineDispatcherFactory,
               EngineContext(ProjectConfiguration(), Platform.JVM)
            )
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(tc, NoopTestScope(testCase, coroutineContext))
         }

         blockingCount.get() shouldBe 1
      }

      test("tests which timeout during a suspending operation should still run the 'after test' listeners").config(
         timeout = 10000.milliseconds
      ) {

         val suspendingCount = AtomicInteger(0)

         // this listener will flick the flag to true, so we know it ran
         val listener = object : AfterTestListener {
            override suspend fun afterAny(testCase: TestCase, result: TestResult) {
               suspendingCount.incrementAndGet()
            }
         }

         val tc = TestCase(
            descriptor = TestTimeoutAfterTestListenerTest::class.toDescriptor().append("wobble"),
            name = TestName("wobble"),
            spec = this@TestTimeoutAfterTestListenerTest,
            test = { delay(1000000) },
            source = sourceRef(),
            type = TestType.Container,
            parent = null,
            config = ResolvedTestConfig.default.copy(
               timeout = 1.milliseconds,
               extensions = listOf(listener),
               blockingTest = false
            ),
         )

         val executor = TestCaseExecutor(
            NoopTestCaseExecutionListener,
            NoopCoroutineDispatcherFactory,
            EngineContext(ProjectConfiguration(), Platform.JVM)
         )
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(tc, NoopTestScope(testCase, coroutineContext))
         }

         suspendingCount.get() shouldBe 1
      }
   }
}
