package io.kotest.engine.js

import io.kotest.common.reflection.bestName
import io.kotest.core.Logger
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.descriptors.DescriptorPaths
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.descriptor
import io.kotest.core.test.TestCase
import io.kotest.engine.listener.TestEngineInitializedContext
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.TestResultBuilder
import kotlinx.coroutines.channels.Channel
import kotlin.reflect.KClass

/**
 * An implementation of [TestEngineListener] that will wait for a spec to be completed, before then emitting
 * all events through a [KotlinJsTestFramework].
 */
internal class JsTestFrameworkTestEngineListener(
   private val framework: KotlinJsTestFramework,
) : TestEngineListener {

   companion object {
      const val TEST_DELIMITER = " » "
      const val SPEC_DELIMITER = "/"
   }

   private val logger = Logger<JsTestFrameworkTestEngineListener>()

   // hold up mocha from exiting before our tests are registered
   // also note that we must only have one receive/send, we can't release specs as they complete like we did in 6.1.8
   // because they run as macrotasks vs. microtasks which break's mochas suite scanning code (it will advance to the
   // next suite, on a delay inside a test, but the next suite isn't registered yet, so it disappears)
   private val channel = Channel<Unit>(1)

   // a multimap of completed tests for a given spec
   private val proxies = mutableMapOf<DescriptorId, MutableList<TestProxy>>()

   override suspend fun engineStarted() {
      // we have to launch the engine inside a test and return a promise, so mocha will wait for the engine to finish
      // otherwise our engine is running in a coroutine and mocha will have exited before we start emitting tests
      // The downside is that we get an extra node in the output (todo, perhaps the IDE plugin can hide this?)
      kotlinJsTestFramework.suite("Kotest", false) {
         kotlinJsTestFramework.test("Executor", false) {
            promise {
               channel.receive() // will suspend this placeholder test until the first real test releases us
            }
         }
      }
   }

   override suspend fun engineInitialized(context: TestEngineInitializedContext) {}

   override suspend fun engineFinished(t: List<Throwable>) {
      // now we can release the mocha anchor test, and mocha will run all the registered describes
      logger.log { "Releasing mocha anchor test" }
      channel.send(Unit)
   }

   override suspend fun specStarted(ref: SpecRef) {
      proxies[ref.descriptor().id] = mutableListOf()
   }

   override suspend fun specIgnored(kclass: KClass<*>, reason: String?) {
      // for ignored specs we can just output immediately as nothing else will be happening
      framework.suite(testNameEscape(kclass.bestName()), true) { }
   }

   override suspend fun specFinished(ref: SpecRef, result: TestResult) {
      // if this spec had no tests, we just skip it
      val tests = proxies[ref.descriptor().id] ?: return

      // now we can output all the tests we collected during the spec run
      // All tests are output at a single level as the kotlin.test apparatus doesn't support nested tests.
      // (If we try to use nested output, then every suite goes to the top level rather than properly nesting.)

      logger.log { "Emitting tests for spec ${ref.fqn}" }
      framework.suite(ref.fqn, false) {
         tests.forEach { proxy ->

            // we have to escape the actual test name (but not the FQN) to remove any periods.
            // if the test name contains the FQN, then only the substring after the FQN is displayed,
            // regardless of where it appears in the test name.

            // remove fqn plus the spec delimiter
            val escapedName = testNameEscape(proxy.name.removePrefix(ref.fqn).removePrefix(SPEC_DELIMITER))

            logger.log { "Emitting test ${ref.fqn} $escapedName" }
            framework.test(escapedName, proxy.result.isIgnored) {
               promise {
                  // just returning without an error is enough to mark as success in jasmine style frameworks
                  proxy.result.errorOrNull?.let { throw it }
               }
            }
         }
      }

      // remove the proxies for this spec as we don't need them anymore
      proxies.remove(ref.descriptor().id)
   }

   override suspend fun testStarted(testCase: TestCase) {
      // we register the proxy when the test starts, so that the output is in registration order
      val path = renderTestPath(testCase)
      val proxy = TestProxy(path, TestResultBuilder.builder().build())
      proxies[testCase.descriptor.spec().id]?.add(proxy)
   }

   override suspend fun testIgnored(testCase: TestCase, reason: String?) {
      val path = renderTestPath(testCase)
      val proxy = TestProxy(path, TestResultBuilder.builder().withIgnoreReason(reason).build())
      proxies[testCase.descriptor.spec().id]?.add(proxy)
   }

   override suspend fun testFinished(testCase: TestCase, result: TestResult) {
      // now we update the proxy with the actual result
      val path = renderTestPath(testCase)
      proxies[testCase.descriptor.spec().id]?.find { it.name == path }?.result = result
   }

   // since the kotlin.test apparatus doesn't like nested JS tests, we flatten
   private fun renderTestPath(testCase: TestCase): String =
      DescriptorPaths.render(testCase.descriptor, SPEC_DELIMITER, TEST_DELIMITER).value
}

internal data class TestProxy(
   val name: String,
   var result: TestResult,
)
