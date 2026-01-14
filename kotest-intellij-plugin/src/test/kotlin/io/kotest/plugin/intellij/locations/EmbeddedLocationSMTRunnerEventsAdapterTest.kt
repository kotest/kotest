package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Test

class EmbeddedLocationSMTRunnerEventsAdapterTest {

   @Test
   fun shouldResetPresentablePathOnEmbeddedLocationOnTestStart() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }


   @Test
   fun shouldResetPresentablePathOnEmbeddedLocationOnTestIgnored() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onTestIgnored(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }

   @Test
   fun shouldResetPresentablePathOnEmbeddedLocationOnTestSuiteStart() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      EmbeddedLocationSMTRunnerEventsAdapter().onSuiteStarted(proxy)
      proxy.locator.shouldBeInstanceOf<EmbeddedLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }
}
