package io.kotest.engine.spec.runners

import io.kotest.common.ExperimentalKotest
import io.kotest.core.concurrency.CoroutineDispatcherFactory
import io.kotest.core.config.Configuration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.core.test.toTestCase
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecExtensions
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.contexts.DuplicateNameHandlingTestContext
import io.kotest.engine.test.scheduler.TestScheduler
import io.kotest.fp.flatMap
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

@ExperimentalKotest
internal class InstancePerLeafSpecRunner(
   listener: TestEngineListener,
   scheduler: TestScheduler,
   private val defaultCoroutineDispatcherFactory: CoroutineDispatcherFactory,
   private val configuration: Configuration,
) : SpecRunner(listener, scheduler, configuration) {

   private val extensions = SpecExtensions(configuration.registry())
   private val results = mutableMapOf<TestCase, TestResult>()

   // keeps track of tests we've already discovered
   private val seen = mutableSetOf<Descriptor>()

   // keeps track of tests we've already notified the listener about
   private val ignored = mutableSetOf<Descriptor>()
   private val started = mutableSetOf<Descriptor>()

   // we keep a count to break ties (first discovered)
   data class Enqueued(val testCase: TestCase, val count: Int)

   private val counter = AtomicInteger(0)

   // the queue contains tests discovered to run next. We always run the tests with the "furthest" path first.
   private val queue = PriorityQueue(Comparator<Enqueued> { o1, o2 ->
      val o1s = o1.testCase.descriptor.depth()
      val o2s = o2.testCase.descriptor.depth()
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
    * The intention of this runner is that each [TestCase] executes in its own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    */
   override suspend fun execute(spec: Spec): Result<Map<TestCase, TestResult>> =
      runCatching {
         spec.materializeAndOrderRootTests(configuration.testCaseOrder).forEach { root ->
            enqueue(root.testCase)
         }
         while (queue.isNotEmpty()) {
            val (testCase, _) = queue.remove()
            executeInCleanSpec(testCase).getOrThrow()
         }
         results
      }

   private suspend fun executeInCleanSpec(test: TestCase): Result<Spec> {
      return createInstance(test.spec::class).flatMap { spec ->
         extensions.intercept(spec) {
            run(spec, test)
         }
      }
   }

   // we need to find the same root test but in the newly created spec
   private suspend fun run(spec: Spec, test: TestCase): Result<Spec> = runCatching {
      val root = spec.materializeAndOrderRootTests(configuration.testCaseOrder)
         .firstOrNull { it.testCase.descriptor.isOnPath(test.descriptor) }
         ?: throw error("Unable to locate root test ${test.descriptor.path()}")
      log { "InstancePerLeafSpecRunner: Starting root test ${root.testCase.descriptor} in search of ${test.descriptor}" }
      extensions.beforeSpec(spec).getOrThrow()
      run(root.testCase, test)
      extensions.afterSpec(spec).getOrThrow()
      spec
   }

   private suspend fun run(test: TestCase, target: TestCase) {
      coroutineScope {
         val context = object : TestContext {

            var open = true

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override suspend fun registerTestCase(nested: NestedTest) {

               val t = nested.toTestCase(test.spec, test)

               // if this test is our target then we definitely run it
               // or if the test is on the path to our target we must run it
               if (t.descriptor.isOnPath(target.descriptor)) {
                  open = false
                  seen.add(t.descriptor)
                  run(t, target)
                  // otherwise, if we're already past our target we're discovering and so
                  // the first discovery we run, the rest we queue
               } else if (target.descriptor.isOnPath(t.descriptor)) {
                  if (seen.add(t.descriptor)) {
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

         val context2 = DuplicateNameHandlingTestContext(configuration.duplicateTestNameMode, context)

         val testExecutor = TestCaseExecutor(
            object : TestCaseExecutionListener {
               override suspend fun testStarted(testCase: TestCase) {
                  if (started.add(testCase.descriptor)) {
                     listener.testStarted(testCase)
                  }
               }

               override suspend fun testIgnored(testCase: TestCase) {
                  if (ignored.add(testCase.descriptor))
                     listener.testIgnored(testCase, null)
               }

               override suspend fun testFinished(testCase: TestCase, result: TestResult) {
                  if (!queue.any { it.testCase.descriptor.isDescendentOf(testCase.descriptor) }) {
                     listener.testFinished(testCase, result)
                  }
               }
            },
            defaultCoroutineDispatcherFactory,
            configuration,
         )

         val result = testExecutor.execute(test, context2)
         results[test] = result
      }
   }
}
