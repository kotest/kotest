package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.ContainerContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.NoopTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.scopes.NoopTestScope
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration.Companion.milliseconds

@Suppress("BlockingMethodInNonBlockingContext")
@EnabledIf(LinuxOnlyGithubCondition::class)
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
            name = TestNameBuilder.builder("wibble").build(),
            spec = this@TestTimeoutAfterTestListenerTest,
            test = { Thread.sleep(1000000) },
            source = SourceRef.None,
            type = TestType.Container,
            parent = null,
            config = TestConfig(
               timeout = 1.milliseconds,
               extensions = listOf(listener),
               blockingTest = true
            ),
         )

         val executor = TestCaseExecutor(
            NoopTestCaseExecutionListener,
            EngineContext(null, Platform.JVM)
         )
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(
               testCase = testCase,
               testScope = NoopTestScope(testCase, coroutineContext),
               specContext = SpecContext.create(),
               containerContext = ContainerContext.create(),
            )
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
            name = TestNameBuilder.builder("wobble").build(),
            spec = this@TestTimeoutAfterTestListenerTest,
            test = { delay(1000000) },
            source = SourceRef.None,
            type = TestType.Container,
            parent = null,
            config = TestConfig(
               timeout = 1.milliseconds,
               extensions = listOf(listener),
               blockingTest = false
            ),
         )

         val executor = TestCaseExecutor(
            NoopTestCaseExecutionListener,
            EngineContext(null, Platform.JVM)
         )
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(
               testCase = testCase,
               testScope = NoopTestScope(testCase, coroutineContext),
               specContext = SpecContext.create(),
               containerContext = ContainerContext.create(),
            )
         }

         suspendingCount.get() shouldBe 1
      }
   }
}
