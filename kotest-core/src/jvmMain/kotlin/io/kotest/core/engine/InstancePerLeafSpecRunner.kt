package io.kotest.core.engine

import io.kotest.core.runtime.*
import io.kotest.core.spec.Spec
import io.kotest.core.test.*
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.Comparator
import kotlin.coroutines.CoroutineContext

class InstancePerLeafSpecRunner(listener: TestEngineListener) : SpecRunner(listener) {

   private val results = ConcurrentHashMap<TestCase, TestResult>()

   // keeps track of tests we've already discovered
   private val seen = ConcurrentHashMap.newKeySet<Description>()

   // keeps track of tests we've already notified the listener about
   private val ignored = ConcurrentLinkedQueue<Description>()
   private val started = ConcurrentLinkedQueue<Description>()

   // we keep a count to break ties (first discovered)
   data class Enqueued(val testCase: TestCase, val count: Int)

   private val counter = AtomicInteger(0)

   private val comparator = Comparator<Enqueued> { o1, o2 ->
      val o1s = o1.testCase.description.names().size
      val o2s = o2.testCase.description.names().size
      if (o1s == o2s) o1.count.compareTo(o2.count) else o2s.compareTo(o1s)
   }

   // the queue contains tests discovered to run next. We always run the tests with the "furthest" path first.
   private val queues: ThreadLocal<PriorityQueue<Enqueued>> = ThreadLocal.withInitial {
      PriorityQueue<Enqueued>(comparator)
   }

   private fun enqueue(testCase: TestCase) {
      queues.get().add(
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
   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> = Try {
      val testCases = spec.rootTests().map { it.testCase }

      runParallel(spec.threads, testCases) {
         executeInCleanSpec(it).getOrThrow()
         while (queues.get().isNotEmpty()) {
            val (testCase, _) = queues.get().remove()
            executeInCleanSpec(testCase).getOrThrow()
         }
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
      val root = spec.rootTests().first { it.testCase.description.isOnPath(test.description) }
      log("Starting root test ${root.testCase.description} in search of ${test.description}")
      run(root.testCase, test)
      spec
   }

   private suspend fun run(test: TestCase, target: TestCase) {
      coroutineScope {
         val context = object : TestContext() {

            var open = true

            // check for duplicate names in the same scope
            val namesInScope = mutableSetOf<String>()

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               if (!namesInScope.add(nested.name))
                  throw IllegalStateException("Cannot add duplicate test ${nested.name}")

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

         val testExecutor = TestExecutor(object : TestExecutionListener {
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
               if (!queues.get().any { it.testCase.description.isDescendentOf(testCase.description) }) {
                  listener.testFinished(testCase, result)
               }
            }
         }, ExecutorExecutionContext)

         val result = testExecutor.execute(test, context)
         results[test] = result
      }
   }
}
