package com.sksamuel.kotest.parallelism

import com.sksamuel.kotest.parallelism.ProjectConfig.projectStart
import com.sksamuel.kotest.parallelism.StateMsg.Status.Finished
import com.sksamuel.kotest.parallelism.StateMsg.Status.Started
import io.kotest.assertions.withClue
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestScope
import io.kotest.inspectors.shouldForAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.mpp.log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.plus
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object ProjectConfig : AbstractProjectConfig() {

   /**
    * Listen for test [StateMsg]s in an independent [CoroutineScope].
    */
   private val TestMsgCollectorScope: CoroutineScope =
      CoroutineScope(Dispatchers.IO) + CoroutineName("TestMsgCollector")

   // set the number of threads so that each test runs in its own thread
   override val parallelism = 10

   /** The expected number of test cases. All should be launched simultaneously. */
   private const val EXPECTED_TEST_COUNT = 8

   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   /** Marks the start of the entire tests, when [beforeProject] is called, before any tests are launched. */
   lateinit var projectStart: TimeMark
      private set

   init {
      // Start listening for launched tests in an independent CoroutineScope.
      testStateMessages
         .onEach { msg -> log { "$msg" } }
         .filter { msg -> msg.status == Started }
         // Count the number of started tests by name
         .runningFold(setOf<String>()) { acc, msg -> acc + msg.testName }
         .map { testNames -> testNames.size }
         // Once all tests are launched, unlock testCompletionLock
         .onEach { startedTestCount ->
            log { "startedTestCount: $startedTestCount" }
            if (startedTestCount == EXPECTED_TEST_COUNT) {
               log {
                  "$EXPECTED_TEST_COUNT tests have been successfully launched simultaneously. " +
                     "Unlocking testCompletionLock and allowing the tests to complete."
               }
               testCompletionLock.unlock()
            }
         }
         .launchIn(TestMsgCollectorScope)
   }

   override suspend fun beforeProject() {
      projectStart = TimeSource.Monotonic.markNow()
   }

   override suspend fun afterProject() {
      val messages = testStateMessages.replayCache

      StateMsg.Status.entries.forEach { status ->
         withClue("Expect exactly $EXPECTED_TEST_COUNT tests have status:$status") {
            messages
               .filter { it.status == status }
               .map { it.testName }
               .shouldContainExactlyInAnyOrder(List(EXPECTED_TEST_COUNT) { "test ${it + 1}" })
         }
      }

      withClue("Expect that all tests started before any test finished") {
         val startedTests = messages.filter { it.status == Started }
         val finishedTests = messages.filter { it.status == Finished }

         startedTests.forEach { startedTest ->
            finishedTests.shouldForAll { finishedTest ->
               startedTest.elapsed shouldBeLessThan finishedTest.elapsed
            }
         }
      }
   }
}

/**
 * Register the start of a test, and wait until all other test cases have been launched.
 *
 * Only when all tests have been launched simultaneously will the test be unlocked and permitted to finish.
 */
suspend fun TestScope.startAndLockTest() {
   withTimeout(10.seconds) {
      testStateMessages.emit(StateMsg(testCase.name.testName, Started))
      testCompletionLock.withLock {
         testStateMessages.emit(StateMsg(testCase.name.testName, Finished))
      }
   }
}

/**
 * Once a test has launched, stop it from completing by using this [Mutex].
 * We want to stop tests from completing to ensure that all tests are launched in parallel.
 */
private val testCompletionLock = Mutex(locked = true)

private val testStateMessages = MutableSharedFlow<StateMsg>(replay = 100)

/**
 * Information about the execution status of a test.
 */
private data class StateMsg(
   val testName: String,
   val status: Status,
   val elapsed: Duration = projectStart.elapsedNow(),
) {
   enum class Status { Started, Finished }
}
