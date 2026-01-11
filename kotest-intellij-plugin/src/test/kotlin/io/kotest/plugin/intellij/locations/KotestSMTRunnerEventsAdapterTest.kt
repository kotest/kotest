package io.kotest.plugin.intellij.locations

import com.intellij.execution.testframework.sm.runner.SMTestProxy
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.Test

class KotestSMTRunnerEventsAdapterTest {

   @Test
   fun shouldResetPresentablePathOnEmbeddedLocation() {
      val proxy = SMTestProxy(
         /* testName = */ "<kotest>io.kotest.Spec.test -- nested</kotest>nested",
         /* isSuite = */ false,
         /* locationUrl = */ "java:suite:io.kotest.Spec/test"
      )
      KotestSMTRunnerEventsAdapter().onTestStarted(proxy)
      proxy.locator.shouldBeInstanceOf<KotestLocationTestLocator>()
      proxy.presentableName shouldBe "nested"
   }
}
