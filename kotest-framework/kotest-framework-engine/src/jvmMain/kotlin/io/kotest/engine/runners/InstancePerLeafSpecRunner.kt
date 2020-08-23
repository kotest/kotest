package io.kotest.engine.runners

import io.kotest.core.DuplicatedTestNameException
import io.kotest.core.spec.Spec
import io.kotest.core.test.Description
import io.kotest.core.test.DescriptionName
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.core.internal.TestCaseExecutor
import io.kotest.core.spec.invokeAfterSpec
import io.kotest.core.spec.invokeBeforeSpec
import io.kotest.core.spec.materializeAndOrderRootTests
import io.kotest.core.test.TestCaseExecutionListener
import io.kotest.engine.toTestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Comparator
import kotlin.coroutines.CoroutineContext

internal class InstancePerLeafSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val results = mutableMapOf<TestCase, TestResult>()

   // keeps track of tests we've already discovered
   private val seen = mutableSetOf<Description>()

   // keeps track of tests we've already notified the listener about
   private val ignored = mutableSetOf<Description>()
   private val started = mutableSetOf<Description>()

   // we keep a count to break ties (first discovered)
   data class Enqueued(val testCase: TestCase, val count: Int)

   private val counter = AtomicInteger(0)

   // the queue contains tests discovered to run next. We always run the tests with the "furthest" path first.
   private val queue = PriorityQueue(Comparator<Enqueued> { o1, o2 ->
      val o1s = o1.testCase.description.names().size
      val o2s = o2.testCase.description.names().size
      if (o1s == o2s) o1.count.compareTo(o2.count) else o2s.compareTo(o1s)
   })

   private fun enqueue(testCase: TestCase) {
      queue.add(
         Enqueued(
            testCase,
            counter.incrementAndGet()
         )
      )
   }

   /**
    * The intention of this runner is that each [TestCase] executes in it's own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    */
   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> =
      Try {
         spec.materializeAndOrderRootTests().forEach { root ->
            enqueue(root.testCase)
         }
         while (queue.isNotEmpty()) {
            val (testCase, _) = queue.remove()
            executeInCleanSpec(testCase).getOrThrow()
         }
         results
      }

   private suspend fun executeInCleanSpec(test: TestCase): Try<Spec> {
      return createInstance(test.spec::class)
         .flatMap { it.invokeBeforeSpec() }
         .flatMap { interceptAndRun(it, test) }
         .flatMap { it.invokeAfterSpec() }
   }

   // we need to find the same root test but in the newly created spec
   private suspend fun interceptAndRun(spec: Spec, test: TestCase): Try<Spec> = Try {
      log("Created new spec instance $spec")
      val root = spec.materializeAndOrderRootTests().first { it.testCase.description.isOnPath(test.description) }
      log("Starting root test ${root.testCase.description} in search of ${test.description}")
      run(root.testCase, test)
      spec
   }

   private suspend fun run(test: TestCase, target: TestCase) {
      coroutineScope {
         val context = object : TestContext {

            var open = true

            // check for duplicate names in the same scope
            val namesInScope = mutableSetOf<DescriptionName.TestName>()

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               if (!namesInScope.add(nested.name))
                  throw DuplicatedTestNameException(nested.name)

               val t = nested.toTestCase(test.spec, test.description)

               // if this test is our target then we definitely run it
               // or if the test is on the path to our target we must run it
               if (t.description.isOnPath(target.description)) {
                  open = false
                  seen.add(t.description)
                  run(t, target)
                  // otherwise if we're already past our target we're discovering and so
                  // the first discovery we run, the rest we queue
               } else if (target.description.isOnPath(t.description)) {
                  if (seen.add(t.description)) {
                     if (open) {
                        open = false
                        run(t, target)
                     } else {
                        enqueue(t)
                     }
                  }
               }
            }
         }

         val testExecutor = TestCaseExecutor(
            object : TestCaseExecutionListener {
               override fun testStarted(testCase: TestCase) {
                  if (started.add(testCase.description)) {
                     listener.testStarted(testCase)
                  }
               }

               override fun testIgnored(testCase: TestCase) {
                  if (ignored.add(testCase.description))
                     listener.testIgnored(testCase, null)
               }

               override fun testFinished(testCase: TestCase, result: TestResult) {
                  if (!queue.any { it.testCase.description.isDescendentOf(testCase.description) }) {
                     listener.testFinished(testCase, result)
                  }
               }
            },
            ExecutorExecutionContext, {}, { t, duration -> toTestResult(t, duration) },
         )

         val result = testExecutor.execute(test, context)
         results[test] = result
      }
   }
}
