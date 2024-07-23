package com.sksamuel.kotest.parallelism

import com.sksamuel.kotest.parallelism.ProjectConfig.projectStart
import com.sksamuel.kotest.parallelism.TestStatus.Status.Finished
import com.sksamuel.kotest.parallelism.TestStatus.Status.Started
import com.sksamuel.kotest.parallelism.TestStatus.Status.TimedOut
import io.kotest.assertions.withClue
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.test.TestScope
import io.kotest.inspectors.shouldForNone
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.core.log
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
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
   // set the number of threads so that each test runs in its own thread
   override val parallelism = 10

   override val concurrentSpecs: Int = ProjectConfiguration.MaxConcurrency

   /** The expected number of test cases. All should be launched simultaneously. */
   private const val EXPECTED_TEST_COUNT = 8

   /**
    * Listen for test [TestStatus]s in an independent [CoroutineScope].
    */
   private val TestStatusCollectorScope: CoroutineScope =
      CoroutineScope(Dispatchers.IO) + CoroutineName("TestStatusCollector")

   /** Marks the start of the entire tests, when [beforeProject] is called, before any tests are launched. */
   internal lateinit var projectStart: TimeMark
      private set

   init {
      // Start listening for launched tests in an independent CoroutineScope.
      testStatuses
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
         .launchIn(TestStatusCollectorScope)
   }

   override suspend fun beforeProject() {
      projectStart = TimeSource.Monotonic.markNow()
   }

   override suspend fun afterProject() {
      val statuses = testStatuses.replayCache

      withClue("testStateMessages:\n" + statuses.joinToString("\n") { " - $it" }) {

         withClue("Expect no tests timed out") {
            statuses.shouldForNone { it.status shouldBe TimedOut }
         }

         val expectedTestNames = listOf(
            "test 1",
            "test 2",
            "test 3",
            "test 4",
            "test 5",
            "test 6",
            "test 7",
            "test 8",
         )

         listOf(Started, Finished).forEach { status ->
            val actualTestNames = statuses.filter { it.status == status }.map { it.testName }

            withClue("Expect exactly $EXPECTED_TEST_COUNT tests have status:$status") {
               actualTestNames shouldContainExactlyInAnyOrder expectedTestNames
            }
         }

         withClue("Expect that no test finished before all tests had started") {
            val lastStartedTest = statuses.filter { it.status == Started }.maxOf { it.elapsed }
            val firstFinishedTest = statuses.filter { it.status == Finished }.minOf { it.elapsed }

            lastStartedTest shouldBeLessThan firstFinishedTest
         }
      }
   }
}

/**
 * Register the start of a test, and suspend until [testCompletionLock] is unlocked.
 *
 * Only when all tests have been launched simultaneously will [testCompletionLock] be unlocked,
 * and the test is permitted to finish.
 */
suspend fun TestScope.startAndLockTest() {
   testStatuses.emit(TestStatus(testCase.name.testName, Started))
   try {
      withTimeout(10.seconds) {
         testCompletionLock.withLock {
            testStatuses.emit(TestStatus(testCase.name.testName, Finished))
         }
      }
   } catch (ex: TimeoutCancellationException) {
      testStatuses.emit(TestStatus(testCase.name.testName, TimedOut))
   }
}

/**
 * Once a test has launched, stop it from completing by using this [Mutex].
 * We want to stop tests from completing to ensure that all tests are launched simultaneously.
 */
private val testCompletionLock = Mutex(locked = true)

private val testStatuses = MutableSharedFlow<TestStatus>(replay = 100)

/**
 * Information about the execution status of a test.
 */
private data class TestStatus(
   val testName: String,
   val status: Status,
   val elapsed: Duration = projectStart.elapsedNow(),
) {
   enum class Status { Started, Finished, TimedOut }
}
