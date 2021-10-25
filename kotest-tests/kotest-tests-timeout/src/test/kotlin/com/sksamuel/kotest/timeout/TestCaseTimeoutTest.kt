package com.sksamuel.kotest.timeout

import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.append
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestName
import io.kotest.core.sourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.config.ResolvedTestConfig
import io.kotest.engine.concurrency.NoopCoroutineDispatcherFactory
import io.kotest.engine.test.NoopTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.contexts.NoopTestContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration

@DelicateCoroutinesApi
@Suppress("BlockingMethodInNonBlockingContext")
class TestCaseTimeoutTest : FunSpec() {
   init {

      test("tests that timeout during a blocking operation should be interrupted").config(
         timeout = Duration.milliseconds(10000),
         blockingTest = true,
      ) {
         val tc = TestCase(
            descriptor = TestCaseTimeoutTest::class.toDescriptor().append("wibble"),
            name = TestName("wibble"),
            spec = this@TestCaseTimeoutTest,
            test = { Thread.sleep(10000000) },
            source = sourceRef(),
            type = TestType.Container,
            parent = null,
            config = ResolvedTestConfig.default.copy(
               timeout = Duration.milliseconds(1),
               blockingTest = true
            ),
         )

         val executor = TestCaseExecutor(NoopTestCaseExecutionListener, NoopCoroutineDispatcherFactory, Configuration())
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(tc, NoopTestContext(testCase, coroutineContext))
         }
      }

      test("tests which timeout during a suspending operation should be cancelled").config(
         timeout = Duration.milliseconds(10000)
      ) {
         val tc = TestCase(
            descriptor = TestCaseTimeoutTest::class.toDescriptor().append("wobble"),
            name = TestName("wobble"),
            spec = this@TestCaseTimeoutTest,
            test = { delay(1000000) },
            source = sourceRef(),
            type = TestType.Container,
            parent = null,
            config = ResolvedTestConfig.default.copy(
               timeout = Duration.milliseconds(1),
            ),
         )

         val executor = TestCaseExecutor(NoopTestCaseExecutionListener, NoopCoroutineDispatcherFactory, Configuration())
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(tc, NoopTestContext(testCase, coroutineContext))
         }
      }
   }
}
