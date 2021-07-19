package io.kotest.engine.spec.runners

import io.kotest.core.config.configuration
import io.kotest.core.execution.ExecutionContext
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Spec
import io.kotest.core.test.NestedTest
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestContext
import io.kotest.core.test.TestResult
import io.kotest.engine.ExecutorExecutionContext
import io.kotest.engine.launchers.TestLauncher
import io.kotest.engine.lifecycle.invokeAfterSpec
import io.kotest.engine.lifecycle.invokeBeforeSpec
import io.kotest.engine.listener.TestEngineListener
import io.kotest.engine.spec.SpecRunner
import io.kotest.engine.spec.materializeAndOrderRootTests
import io.kotest.engine.test.TestCaseExecutionListener
import io.kotest.engine.test.TestCaseExecutor
import io.kotest.engine.test.attach
import io.kotest.engine.test.names.DuplicateTestNameHandler
import io.kotest.engine.test.toTestResult
import io.kotest.fp.Try
import io.kotest.mpp.log
import kotlinx.coroutines.coroutineScope
import java.util.PriorityQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.CoroutineContext

internal class InstancePerLeafSpecRunner(
   private val executionContext: ExecutionContext,
   listener: TestEngineListener,
   launcher: TestLauncher
) : SpecRunner(listener, launcher) {

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
    * The intention of this runner is that each [TestCase] executes in it's own instance
    * of the containing [Spec] class. Therefore, when we begin executing a test case from
    * the queue, we must first instantiate a new spec, and begin execution on _that_ instance.
    */
   override suspend fun execute(spec: Spec): Try<Map<TestCase, TestResult>> =
      Try {
         spec.materializeAndOrderRootTests(executionContext).forEach { root ->
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
      log { "InstancePerLeafSpecRunner: Created new spec instance $spec" }
      val root =
         spec.materializeAndOrderRootTests(executionContext).firstOrNull { it.testCase.descriptor.isOnPath(test.descriptor) }
            ?: throw error("Unable to locate root test ${test.descriptor.testPath()}")
      log { "InstancePerLeafSpecRunner: Starting root test ${root.testCase.descriptor} in search of ${test.descriptor}" }
      run(root.testCase, test)
      spec
   }

   private suspend fun run(test: TestCase, target: TestCase) {
      coroutineScope {
         val context = object : TestContext {

            var open = true

            private val handler = DuplicateTestNameHandler(configuration.duplicateTestNameMode)

            override val testCase: TestCase = test
            override val coroutineContext: CoroutineContext = this@coroutineScope.coroutineContext
            override val executionContext: ExecutionContext = this@InstancePerLeafSpecRunner.executionContext

            override suspend fun registerTestCase(nested: NestedTest) {

               val t = nested.attach(testCase, handler.handle(nested.name), this@InstancePerLeafSpecRunner.executionContext)

               // if this test is our target then we definitely run it
               // or if the test is on the path to our target we must run it
               if (t.descriptor.isOnPath(target.descriptor)) {
                  open = false
                  seen.add(t.descriptor)
                  run(t, target)
                  // otherwise if we're already past our target we're discovering and so
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

         val testExecutor = TestCaseExecutor(
            object : TestCaseExecutionListener {
               override fun testStarted(testCase: TestCase) {
                  if (started.add(testCase.descriptor)) {
                     listener.testStarted(testCase)
                  }
               }

               override fun testIgnored(testCase: TestCase) {
                  if (ignored.add(testCase.descriptor))
                     listener.testIgnored(testCase, null)
               }

               override fun testFinished(testCase: TestCase, result: TestResult) {
                  if (!queue.any { it.testCase.descriptor.isDescendentOf(testCase.descriptor) }) {
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
