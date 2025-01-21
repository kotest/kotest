package com.sksamuel.kotest.engine.test.timeout

import io.kotest.core.Platform
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.descriptors.append
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.core.test.config.TestConfig
import io.kotest.engine.descriptors.toDescriptor
import io.kotest.engine.interceptors.EngineContext
import io.kotest.engine.spec.interceptor.SpecContext
import io.kotest.engine.test.NoopTestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.scopes.NoopTestScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxCondition::class)
@Suppress("BlockingMethodInNonBlockingContext")
class TestTimeoutTest : FunSpec() {
   init {

      test("tests that timeout during a blocking operation should be interrupted").config(
         timeout = 10000.milliseconds,
         blockingTest = true,
      ) {
         val tc = TestCase(
            descriptor = TestTimeoutTest::class.toDescriptor().append("wibble"),
            name = TestNameBuilder.builder("wibble").build(),
            spec = this@TestTimeoutTest,
            test = { Thread.sleep(10000000) },
            source = SourceRef.None,
            type = TestType.Container,
            parent = null,
            config = TestConfig(
               timeout = 1.milliseconds,
               blockingTest = true
            ),
         )

         val executor = TestCaseExecutor(
            NoopTestCaseExecutionListener,
            EngineContext(null, Platform.JVM),
         )
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(tc, NoopTestScope(testCase, coroutineContext), SpecContext.create())
         }
      }

      test("tests which timeout during a suspending operation should be cancelled").config(
         timeout = 10000.milliseconds
      ) {
         val tc = TestCase(
            descriptor = TestTimeoutTest::class.toDescriptor().append("wobble"),
            name = TestNameBuilder.builder("wobble").build(),
            spec = this@TestTimeoutTest,
            test = { delay(1000000) },
            source = SourceRef.None,
            type = TestType.Container,
            parent = null,
            config = TestConfig(
               timeout = 1.milliseconds,
            ),
         )

         val executor = TestCaseExecutor(
            NoopTestCaseExecutionListener,
            EngineContext(null, Platform.JVM),
         )
         // needs to run on a separate thread, so we don't interrupt our own thread
         withContext(Dispatchers.IO) {
            executor.execute(tc, NoopTestScope(testCase, coroutineContext), SpecContext.create())
         }
      }
   }
}
