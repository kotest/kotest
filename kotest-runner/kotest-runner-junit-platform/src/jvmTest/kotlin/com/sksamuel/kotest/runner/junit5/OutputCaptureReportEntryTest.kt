package com.sksamuel.kotest.runner.junit5

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.CapturedOutput
import io.kotest.engine.test.TestResult
import io.kotest.engine.test.names.DisplayNameFormatting
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit.platform.EngineDescriptorBuilder
import io.kotest.runner.junit.platform.JUnitTestEngineListener
import io.kotest.runner.junit.platform.KotestJunitPlatformTestEngine
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.reporting.ReportEntry
import kotlin.time.Duration.Companion.milliseconds

@EnabledIf(LinuxOnlyGithubCondition::class)
class OutputCaptureReportEntryTest : FunSpec({

   val root = EngineDescriptorBuilder
      .builder(UniqueId.forEngine(KotestJunitPlatformTestEngine.ENGINE_ID))
      .withSpecs(listOf(SpecRef.Reference(OutputSpec::class)))
      .build()

   val container = TestCase(
      OutputSpec::class.toDescriptor().append("container"),
      TestNameBuilder.builder("container").build(),
      OutputSpec(),
      {},
      SourceRef.None,
      TestType.Container,
   )

   val leaf = TestCase(
      container.descriptor.append("leaf"),
      TestNameBuilder.builder("leaf").build(),
      container.spec,
      {},
      SourceRef.None,
      TestType.Test,
      parent = container,
   )

   test("captured output is published as stdout and stderr report entries just before its test finishes") {
      val track = ReportEntryTrackingListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(OutputSpec::class))
      listener.testStarted(container)
      listener.testStarted(leaf)
      listener.testOutputCaptured(
         leaf,
         CapturedOutput(
            listOf(
               CapturedOutput.Chunk(CapturedOutput.Stream.Out, "out 1\n"),
               CapturedOutput.Chunk(CapturedOutput.Stream.Err, "err 1\n"),
               CapturedOutput.Chunk(CapturedOutput.Stream.Out, "out 2\n"),
            ),
            truncated = false,
         ),
      )
      listener.testFinished(leaf, TestResult.Failure(1.milliseconds, AssertionError("boom")))
      listener.testFinished(container, TestResult.Success(1.milliseconds))
      listener.specFinished(SpecRef.Reference(OutputSpec::class), TestResult.Success(1.milliseconds))

      track.events shouldBe listOf(
         "started OutputSpec",
         "started container",
         "started leaf",
         "entry leaf {stdout=out 1\nout 2\n, stderr=err 1\n}",
         "finished leaf",
         "finished container",
         "finished OutputSpec",
      )
   }

   test("a test with no captured output publishes no report entry") {
      val track = ReportEntryTrackingListener()
      val listener = JUnitTestEngineListener(track, root, DisplayNameFormatting(null))
      listener.specStarted(SpecRef.Reference(OutputSpec::class))
      listener.testStarted(container)
      listener.testStarted(leaf)
      listener.testFinished(leaf, TestResult.Failure(1.milliseconds, AssertionError("boom")))
      listener.testFinished(container, TestResult.Success(1.milliseconds))
      listener.specFinished(SpecRef.Reference(OutputSpec::class), TestResult.Success(1.milliseconds))

      track.events.filter { it.startsWith("entry") } shouldBe emptyList()
   }
})

private class OutputSpec : FunSpec()

private class ReportEntryTrackingListener : EngineExecutionListener {

   val events = mutableListOf<String>()

   override fun dynamicTestRegistered(testDescriptor: TestDescriptor) {}

   override fun executionStarted(testDescriptor: TestDescriptor) {
      events.add("started ${testDescriptor.displayName}")
   }

   override fun executionFinished(testDescriptor: TestDescriptor, testExecutionResult: TestExecutionResult) {
      events.add("finished ${testDescriptor.displayName}")
   }

   override fun reportingEntryPublished(testDescriptor: TestDescriptor, entry: ReportEntry) {
      events.add("entry ${testDescriptor.displayName} ${entry.keyValuePairs}")
   }
}
