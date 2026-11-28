package com.sksamuel.kotest.engine.test.output

import io.kotest.common.ExperimentalKotest
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.config.OutputCapture
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.engine.EngineResult
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.concurrency.SpecExecutionMode
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.test.CapturedOutput
import io.kotest.engine.test.CurrentTestCase
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.maps.shouldContainKey
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldEndWith
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.engine.concurrency.TestExecutionMode
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import kotlin.concurrent.thread

@OptIn(ExperimentalKotest::class)
class OutputCaptureTest : FunSpec() {
   init {

      test("a failed test's output is reported, from every thread its coroutines ran on, and a passing test's is not") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, FailingAndPassingSpec::class)

         listener.outputs shouldNotContainKey "passes"
         val output = listener.outputs.getValue("fails")
         output.stdout.lf() shouldContain "own line"
         output.stdout.lf() shouldContain "after a dispatcher switch"
         output.stdout.lf() shouldContain "from a child coroutine"
         output.stdout.lf() shouldContain "from beforeTest"
         output.stdout.lf() shouldNotContain "passing line"
         output.stderr.lf() shouldContain "own error line"
         output.truncated.shouldBeFalse()
      }

      test("output is reported before the test is finished") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, FailingAndPassingSpec::class)
         listener.events.filter { it.endsWith(":fails") } shouldBe listOf("output:fails", "finished:fails")
      }

      test("a failed test's output excludes the output of a test running at the same time") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, FailsWhileNeighbourPrintsSpec::class, NeighbourSpec::class)

         listener.outputs.getValue("fails while a neighbour prints").stdout.lf() shouldBe "before neighbour\nafter neighbour\n"
         listener.outputs shouldNotContainKey "prints while the other spec's test runs"
      }

      test("a parent test's output excludes its child's, and the child's excludes its parent's") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.Always, NestedSpec::class)

         listener.outputs.getValue("child").stdout.lf() shouldBe "child line\n"
         listener.outputs.getValue("parent").stdout.lf() shouldBe "parent before\nparent after\n"
      }

      test("Always reports a passing test's output") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.Always, FailingAndPassingSpec::class)
         listener.outputs.getValue("passes").stdout.lf() shouldBe "from beforeTest\npassing line\n"
      }

      test("Never leaves the standard streams alone and reports nothing") {
         val listener = OutputCollectingListener()
         val out = System.out
         run(listener, OutputCapture.Never, StreamIdentitySpec::class)
         StreamIdentitySpec.seen shouldBeSameInstanceAs out
         listener.outputs.keys.shouldBeEmpty()
      }

      test("the standard streams are restored when the engine finishes") {
         val out = System.out
         val err = System.err
         run(OutputCollectingListener(), OutputCapture.OnFailure, FailingAndPassingSpec::class)
         System.out shouldBeSameInstanceAs out
         System.err shouldBeSameInstanceAs err
      }

      test("a test that writes more than the limit keeps its newest output and is marked truncated") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, NoisySpec::class, maxBytes = 20)

         val output = listener.outputs.getValue("noisy")
         output.truncated.shouldBeTrue()
         output.stdout.lf() shouldEndWith "line 100\n"
         output.stdout.lf() shouldNotContain "line 1\n"
      }

      test("a spec can enable output capture when the project does not") {
         val listener = OutputCollectingListener()
         val out = System.out
         run(listener, OutputCapture.Never, SpecLevelCaptureSpec::class, FailingAndPassingSpec::class)

         listener.outputs.getValue("spec level fails").stdout.lf() shouldBe "spec level line\n"
         listener.outputs shouldNotContainKey "fails"
         System.out shouldBeSameInstanceAs out
      }

      test("a test can override the spec's output capture") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.Never, TestLevelCaptureSpec::class)

         listener.outputs.getValue("always").stdout.lf() shouldBe "always line\n"
         listener.outputs shouldNotContainKey "default"
      }

      test("with spec threads, output from a thread the spec created goes to the spec's running test, not a neighbour's") {
         val listener = OutputCollectingListener()
         run(
            listener, OutputCapture.OnFailure, SpecThreadsFailingSpec::class, SpecThreadsNeighbourSpec::class,
            specThreads = true,
         )

         listener.outputs.getValue("fails with pool output").stdout.lf() shouldBe
            "own line\nfrom the spec's pool\nfrom a thread the test started\n"
         listener.outputs shouldNotContainKey "neighbour with pool output"
      }

      test("without spec threads, output from a thread the spec created is not captured") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, SpecThreadsFailingSpec::class, SpecThreadsNeighbourSpec::class)

         listener.outputs.getValue("fails with pool output").stdout.lf() shouldBe "own line\n"
      }

      test("with spec threads, output from the JVM's shared pools is not attributed") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, SharedPoolSpec::class, specThreads = true)

         listener.outputs.getValue("fails using shared pools").stdout.lf() shouldBe "own line\n"
      }

      test("with spec threads, output from a spec's thread is not attributed while the spec runs tests concurrently") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.Always, ConcurrentTestsSpec::class, specThreads = true)

         listener.outputs.getValue("a").stdout.lf() shouldBe "a line\n"
         listener.outputs.getValue("b").stdout.lf() shouldBe "b line\n"
      }

      test("with spec threads, a shared pool's lazily created thread belongs to the spec that submitted work") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, OwnedSharedPoolSpec::class, specThreads = true)

         listener.outputs.getValue("submits to a shared pool").stdout.lf() shouldBe "own line\nfrom the shared pool\n"
      }

      test("with spec threads, threads from an unowned thread factory never belong to a spec") {
         val listener = OutputCollectingListener()
         run(
            listener, OutputCapture.OnFailure, UnownedSharedPoolSpecA::class, UnownedSharedPoolSpecB::class,
            specThreads = true,
         )

         listener.outputs.getValue("a submits to an unowned pool").stdout.lf() shouldBe "a line\n"
         listener.outputs.getValue("b submits to an unowned pool").stdout.lf() shouldBe "b line\n"
      }

      test("an unowned thread factory keeps the delegate's thread name and daemon flag") {
         val delegate = ThreadFactory { r -> Thread(r, "custom-thread").apply { isDaemon = true } }
         val thread = CurrentTestCase.unownedThreadFactory(delegate).newThread { }
         thread.name shouldBe "custom-thread"
         thread.isDaemon.shouldBeTrue()
      }

      test("a single write larger than the limit keeps its newest bytes and is marked truncated") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, OversizedWriteSpec::class, maxBytes = 20)

         val output = listener.outputs.getValue("one oversized write")
         output.truncated.shouldBeTrue()
         output.stdout.lf() shouldBe "x".repeat(10) + "0123456789"
      }

      test("with spec threads, output from a spec's thread while a non-capturing child runs is not given to its parent") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.Always, NeverChildSpec::class, specThreads = true)

         listener.outputs.getValue("capturing parent").stdout.lf() shouldBe "parent line\n"
         listener.outputs shouldNotContainKey "non-capturing child"
      }

      test("under InstancePerLeaf, a container's output is reported once, before it finishes") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, InstancePerLeafSpec::class)

         listener.outputs.getValue("failing container").stdout.lf() shouldBe "container line\n"
         listener.outputs.getValue("leaf a").stdout.lf() shouldBe "leaf a line\n"
         listener.events.filter { it.endsWith(":failing container") } shouldBe
            listOf("output:failing container", "finished:failing container")
      }

      test("a negative limit fails the run up front with a message naming the setting") {
         val listener = OutputCollectingListener()
         val result = run(listener, OutputCapture.OnFailure, FailingAndPassingSpec::class, maxBytes = -1)

         result.errors.map { it.message } shouldBe listOf("outputCaptureMaxBytes must not be negative, but was -1")
         listener.results.keys.shouldBeEmpty()
      }

      test("a limit of zero keeps no output and marks it truncated") {
         val listener = OutputCollectingListener()
         run(listener, OutputCapture.OnFailure, NoisySpec::class, maxBytes = 0)

         val output = listener.outputs["noisy"]
         // nothing is kept, so there is no output to report
         output shouldBe null
      }

      test("output from threads no test runs on and from listeners is not captured") {
         val listener = OutputCollectingListener(printOnTestStarted = true)
         run(listener, OutputCapture.OnFailure, UnattributedSpec::class)

         val output = listener.outputs.getValue("fails")
         output.stdout.lf() shouldBe "own line\n"
      }
   }

   private suspend fun run(
      listener: OutputCollectingListener,
      mode: OutputCapture,
      vararg specs: kotlin.reflect.KClass<out FunSpec>,
      maxBytes: Int? = null,
      specThreads: Boolean? = null,
   ): EngineResult {
      val config = object : AbstractProjectConfig() {
         override val outputCapture = mode
         override val outputCaptureMaxBytes = maxBytes
         override val outputCaptureSpecThreads = specThreads
         override val specExecutionMode = SpecExecutionMode.Concurrent
      }
      return TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(config)
         .withSpecRefs(specs.map { SpecRef.Reference(it) })
         .execute()
   }
}

// println writes the platform line separator; the assertions are written with \n
private fun String.lf(): String = replace(System.lineSeparator(), "\n")

private class OutputCollectingListener(private val printOnTestStarted: Boolean = false) : AbstractTestEngineListener() {

   val outputs = mutableMapOf<String, CapturedOutput>()
   val events = mutableListOf<String>()
   val results = mutableMapOf<String, io.kotest.engine.test.TestResult>()

   override suspend fun testStarted(testCase: TestCase) {
      if (printOnTestStarted) println("listener line")
   }

   override suspend fun testOutputCaptured(testCase: TestCase, output: CapturedOutput) {
      outputs[testCase.name.name] = output
      events.add("output:${testCase.name.name}")
   }

   override suspend fun testFinished(testCase: TestCase, result: io.kotest.engine.test.TestResult) {
      events.add("finished:${testCase.name.name}")
      results[testCase.name.name] = result
   }
}

private class FailingAndPassingSpec : FunSpec() {
   init {
      beforeTest { println("from beforeTest") }

      test("fails") {
         println("own line")
         System.err.println("own error line")
         withContext(Dispatchers.Default) { println("after a dispatcher switch") }
         launch(Dispatchers.IO) { println("from a child coroutine") }.join()
         error("boom")
      }

      test("passes") {
         println("passing line")
      }
   }
}

private object Neighbour {
   val printed = CompletableDeferred<Unit>()
   val started = CompletableDeferred<Unit>()
}

private class FailsWhileNeighbourPrintsSpec : FunSpec() {
   init {
      test("fails while a neighbour prints") {
         println("before neighbour")
         Neighbour.started.complete(Unit)
         Neighbour.printed.await()
         println("after neighbour")
         error("boom")
      }
   }
}

private class NeighbourSpec : FunSpec() {
   init {
      test("prints while the other spec's test runs") {
         Neighbour.started.await()
         repeat(20) { println("neighbour line $it") }
         Neighbour.printed.complete(Unit)
      }
   }
}

private class NestedSpec : FunSpec() {
   init {
      context("parent") {
         println("parent before")
         test("child") {
            println("child line")
         }
         println("parent after")
      }
   }
}

private class StreamIdentitySpec : FunSpec() {
   companion object {
      var seen: Any? = null
   }

   init {
      test("sees System.out") {
         seen = System.out
      }
   }
}

private class NoisySpec : FunSpec() {
   init {
      test("noisy") {
         (1..100).forEach { println("line $it") }
         error("boom")
      }
   }
}

private class UnattributedSpec : FunSpec() {
   init {
      test("fails") {
         println("own line")
         thread { println("unattributed line") }.join()
         error("boom")
      }
   }
}

private class SpecLevelCaptureSpec : FunSpec() {
   init {
      outputCapture = OutputCapture.OnFailure
      test("spec level fails") {
         println("spec level line")
         error("boom")
      }
   }
}

private class TestLevelCaptureSpec : FunSpec() {
   init {
      test("default") {
         println("default line")
      }
      test("always").config(outputCapture = OutputCapture.Always) {
         println("always line")
      }
   }
}

private object SpecThreadsLatch {
   val started = CompletableDeferred<Unit>()
   val printed = CompletableDeferred<Unit>()
}

private class SpecThreadsFailingSpec : FunSpec() {
   // the pool's thread is created while the spec is constructed, before any test runs
   private val pool = Executors.newSingleThreadExecutor().also { it.submit { }.get() }

   init {
      test("fails with pool output") {
         println("own line")
         SpecThreadsLatch.started.complete(Unit)
         SpecThreadsLatch.printed.await()
         pool.submit { println("from the spec's pool") }.get()
         thread { println("from a thread the test started") }.join()
         error("boom")
      }
      afterSpec { pool.shutdown() }
   }
}

private class SpecThreadsNeighbourSpec : FunSpec() {
   private val pool = Executors.newSingleThreadExecutor().also { it.submit { }.get() }

   init {
      test("neighbour with pool output") {
         SpecThreadsLatch.started.await()
         pool.submit { println("from the neighbour's pool") }.get()
         SpecThreadsLatch.printed.complete(Unit)
      }
      afterSpec { pool.shutdown() }
   }
}

private class SharedPoolSpec : FunSpec() {
   init {
      test("fails using shared pools") {
         println("own line")
         CompletableFuture.runAsync { println("from the common pool") }.get()
         // a coroutine the test does not own, on a kotlinx.coroutines dispatcher thread
         CoroutineScope(Dispatchers.Default).launch { println("from an unowned coroutine") }.join()
         error("boom")
      }
   }
}

private class ConcurrentTestsSpec : FunSpec() {
   private val pool = Executors.newSingleThreadExecutor().also { it.submit { }.get() }

   init {
      testExecutionMode = TestExecutionMode.Concurrent
      val bothRunning = CountDownLatch(2)
      val poolWritten = CountDownLatch(1)
      test("a") {
         bothRunning.countDown()
         withContext(Dispatchers.IO) { bothRunning.await() }
         println("a line")
         pool.submit { println("from the pool while both run") }.get()
         poolWritten.countDown()
      }
      test("b") {
         bothRunning.countDown()
         withContext(Dispatchers.IO) { bothRunning.await() }
         println("b line")
         // still running while a's pool write happens, so the spec is running two tests at once
         withContext(Dispatchers.IO) { poolWritten.await() }
      }
      afterSpec { pool.shutdown() }
   }
}

// shared by specs, and created by whichever spec first touches it, like a lazily initialised global pool
private object OwnedSharedPool {
   val pool: ExecutorService = Executors.newCachedThreadPool()
}

private object UnownedSharedPool {
   @OptIn(ExperimentalKotest::class)
   val pool: ExecutorService = Executors.newCachedThreadPool(CurrentTestCase.unownedThreadFactory())
}

private class OwnedSharedPoolSpec : FunSpec() {
   init {
      test("submits to a shared pool") {
         println("own line")
         OwnedSharedPool.pool.submit { println("from the shared pool") }.get()
         error("boom")
      }
   }
}

private object UnownedLatch {
   val aSubmitted = CompletableDeferred<Unit>()
}

private class UnownedSharedPoolSpecA : FunSpec() {
   init {
      test("a submits to an unowned pool") {
         println("a line")
         UnownedSharedPool.pool.submit { println("from the unowned pool for a") }.get()
         UnownedLatch.aSubmitted.complete(Unit)
         error("boom")
      }
   }
}

private class UnownedSharedPoolSpecB : FunSpec() {
   init {
      test("b submits to an unowned pool") {
         println("b line")
         UnownedLatch.aSubmitted.await()
         // likely reuses the thread a's submission created, which would otherwise belong to a's spec
         UnownedSharedPool.pool.submit { println("from the unowned pool for b") }.get()
         error("boom")
      }
   }
}

private class OversizedWriteSpec : FunSpec() {
   init {
      test("one oversized write") {
         // a single write to the stream, more than twice the limit
         print("x".repeat(100) + "0123456789")
         error("boom")
      }
   }
}

private class NeverChildSpec : FunSpec() {
   private val pool = Executors.newSingleThreadExecutor().also { it.submit { }.get() }

   init {
      context("capturing parent") {
         println("parent line")
         test("non-capturing child").config(outputCapture = OutputCapture.Never) {
            pool.submit { println("from the spec's pool while the child runs") }.get()
         }
      }
      afterSpec { pool.shutdown() }
   }
}

@Suppress("DEPRECATION")
private class InstancePerLeafSpec : FunSpec() {
   init {
      isolationMode = IsolationMode.InstancePerLeaf
      context("failing container") {
         println("container line")
         test("leaf a") {
            println("leaf a line")
            error("boom")
         }
         test("leaf b") { }
         error("container boom")
      }
   }
}
