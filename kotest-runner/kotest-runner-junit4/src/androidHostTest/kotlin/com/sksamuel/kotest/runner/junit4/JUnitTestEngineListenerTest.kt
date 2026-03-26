package com.sksamuel.kotest.runner.junit4

import io.kotest.core.descriptors.toDescriptor
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.source.SourceRef
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestType
import io.kotest.engine.test.TestResultBuilder
import io.kotest.matchers.shouldBe
import io.kotest.runner.junit4.JUnitTestEngineListener
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import java.io.IOException

class JUnitTestEngineListenerTest : FunSpec({

   val ref = SpecRef.Reference(JUnitTestEngineListenerTest::class)

   test("specStarted should be a no-op") {
      val notifier = RunNotifier()
      val listener = CollectingRunListener()
      notifier.addListener(listener)
      JUnitTestEngineListener(notifier).specStarted(ref)
      listener.testsStarted.size shouldBe 0
      listener.testsSuiteStarted.size shouldBe 0
   }

   test("should add a placeholder for errors in specs") {
      val notifier = RunNotifier()
      val listener = CollectingRunListener()
      notifier.addListener(listener)
      JUnitTestEngineListener(notifier).specFinished(
         ref,
         TestResultBuilder.builder().withError(IOException("boom")).build()
      )
      listener.testsStarted.single().methodName shouldBe "IOException"
      listener.failures.single().message shouldBe "boom"
   }
   test("errors in tests should be displayed") {

      val tc = TestCase(
         JUnitTestEngineListenerTest::class.toDescriptor().append("foo"),
         TestNameBuilder.builder("foo").build(),
         JUnitTestEngineListenerTest(),
         {},
         SourceRef.None,
         TestType.Test
      )

      val notifier = RunNotifier()
      val listener = CollectingRunListener()
      notifier.addListener(listener)
      JUnitTestEngineListener(notifier).testFinished(
         tc,
         TestResultBuilder.builder().withError(IOException("boom")).build()
      )
      listener.testsFinished.single().methodName shouldBe "foo"
      listener.failures.single().message shouldBe "boom"
   }

   test("failures in tests should be displayed") {

      val tc = TestCase(
         JUnitTestEngineListenerTest::class.toDescriptor().append("foo"),
         TestNameBuilder.builder("foo").build(),
         JUnitTestEngineListenerTest(),
         {},
         SourceRef.None,
         TestType.Test
      )

      val notifier = RunNotifier()
      val listener = CollectingRunListener()
      notifier.addListener(listener)
      JUnitTestEngineListener(notifier).testFinished(
         tc,
         TestResultBuilder.builder().withError(IOException("boom")).build()
      )
      listener.testsFinished.single().methodName shouldBe "foo"
      listener.failures.single().message shouldBe "boom"
   }

})

class CollectingRunListener : RunListener() {

   val testsStarted = mutableListOf<Description>()
   val testsSuiteStarted = mutableListOf<Description>()
   val testsFinished = mutableListOf<Description>()
   val failures = mutableListOf<Failure>()

   override fun testStarted(description: Description) {
      testsStarted.add(description)
   }

   override fun testSuiteStarted(description: Description) {
      testsSuiteStarted.add(description)
   }

   override fun testFinished(description: Description) {
      testsFinished.add(description)
   }

   override fun testFailure(failure: Failure) {
      failures.add(failure)
   }
}
