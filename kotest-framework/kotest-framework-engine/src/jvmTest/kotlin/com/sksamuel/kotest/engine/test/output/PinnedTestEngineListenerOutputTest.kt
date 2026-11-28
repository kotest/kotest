package com.sksamuel.kotest.engine.test.output

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.listener.AbstractTestEngineListener
import io.kotest.engine.listener.PinnedTestEngineListener
import io.kotest.engine.test.CapturedOutput
import io.kotest.engine.test.TestResult
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.milliseconds

class PinnedTestEngineListenerOutputTest : FunSpec() {
   init {

      fun leaf(name: String) = TestCase(
         PinnedTestEngineListenerOutputTest::class.toDescriptor().append(name),
         TestNameBuilder.builder(name).build(),
         this@PinnedTestEngineListenerOutputTest,
         {},
         SourceRef.None,
         TestType.Test,
      )

      test("a queued test's output is delivered between its own start and finish") {
         val a = leaf("a")
         val b = leaf("b")
         val output = CapturedOutput(listOf(CapturedOutput.Chunk(CapturedOutput.Stream.Out, "x")), truncated = false)

         val events = mutableListOf<String>()
         val pinned = PinnedTestEngineListener(object : AbstractTestEngineListener() {
            override suspend fun testStarted(testCase: TestCase) {
               events.add("started ${testCase.name.name}")
            }

            override suspend fun testOutputCaptured(testCase: TestCase, output: CapturedOutput) {
               events.add("output ${testCase.name.name}")
            }

            override suspend fun testFinished(testCase: TestCase, result: TestResult) {
               events.add("finished ${testCase.name.name}")
            }
         })

         // a and b run concurrently, and b's events arrive while a is still open
         pinned.testStarted(a)
         pinned.testStarted(b)
         pinned.testOutputCaptured(b, output)
         pinned.testFinished(b, TestResult.Success(1.milliseconds))
         pinned.testOutputCaptured(a, output)
         pinned.testFinished(a, TestResult.Success(1.milliseconds))

         events shouldBe listOf(
            "started a",
            "output a",
            "finished a",
            "started b",
            "output b",
            "finished b",
         )
      }
   }
}
