package com.sksamuel.kotest.ignored

import io.kotest.assertions.withClue
import io.kotest.core.NamedTag
import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Order
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.Enabled
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestCaseConfig
import io.kotest.core.test.TestCaseOrder
import io.kotest.core.test.TestResult
import io.kotest.core.test.TestStatus
import io.kotest.core.test.xdisabledMessage
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import java.util.UUID
import kotlin.reflect.KClass

private val tag = NamedTag("SkippedReason")

private const val isEnabledExtensionTestName = "is enabled extension should report why a test was skipped"

private var fakeRan = false
private val ignoredWithFunction = "This test was ignored by enabledOrReasonIf due to: ${UUID.randomUUID()}"
private val ignoredWithExtension = "This test was ignored by EnabledExtension due to: ${UUID.randomUUID()}"

@OptIn(ExperimentalKotest::class)
private val skippedExtension = object : EnabledExtension {
   override suspend fun isEnabled(descriptor: Descriptor): Enabled =
      if (descriptor.name.value == isEnabledExtensionTestName)
         Enabled.disabled(ignoredWithExtension)
      else
         Enabled.enabled
}

private val skippedListener = object : TestListener {
   override val name = "Ignored Test Reason Listener"

   val reasons = mutableSetOf<String>()

   override suspend fun finalizeSpec(kclass: KClass<out Spec>, results: Map<TestCase, TestResult>) {
      reasons.addAll(results.values.filter { it.status == TestStatus.Ignored }.map { it.reason ?: "" })
      configuration.deregisterListener(this)
      configuration.deregisterExtension(skippedExtension)
   }
}

@Order(0)
@Isolate // Isolate because this spec registers global extensions and listeners which have state
class IgnoredTestReasonFake : FunSpec({
   tags(tag)
   testOrder = TestCaseOrder.Sequential

   beforeSpec {
      configuration.registerExtension(skippedExtension)
      configuration.registerListener(skippedListener)
   }

   test("reasons should be empty") {
      skippedListener.reasons.shouldBeEmpty()
   }

   test("expected to be ignored by enabledOrReasonIf").config(enabledOrReasonIf = { Enabled.disabled(ignoredWithFunction) }) {
      throw RuntimeException()
   }

   test("expected to be ignored by enabled").config(enabled = false) {
      throw RuntimeException()
   }

   test("expected to be ignored by enabledIf").config(enabledIf = { false }) {
      throw RuntimeException()
   }

   test(isEnabledExtensionTestName) {
      throw RuntimeException()
   }

   xtest("expected to be ignored by xdisabled") {
      throw RuntimeException()
   }

   afterSpec {
      fakeRan = true
   }
})

@Order(1)
class IgnoredTestReasonSpec : FunSpec({
   tags(tag)
   defaultTestConfig = TestCaseConfig(enabledOrReasonIf = {
      if (fakeRan) Enabled.enabled else Enabled.disabled("The spec this depends on [IgnoredTestReasonFake] didn't run")
   })

   test("IgnoredTestReasonFake should have six skipped tests") {
      skippedListener.reasons.shouldHaveSize(5)
   }

   test("enabledOrReasonIf should report the reason for skipping") {
      skippedListener.reasons.shouldContain(ignoredWithFunction)
   }

   test("EnabledExtension should report the reason for skipping") {
      skippedListener.reasons.shouldContain(ignoredWithExtension)
   }

   test("xdisabled should report the reason for skipping") {
      skippedListener.reasons.shouldContain(xdisabledMessage.reason)
   }

   test("enabled should report some reason for skipping") {
      val expectedSuffix = "is disabled by enabled property in config"

      withClue("The listener should have a reason that ends with $expectedSuffix") {
         skippedListener.reasons.filter { it.endsWith(expectedSuffix) }.shouldHaveSize(1)
      }
   }

   test("enabledIf should report some reason for skipping") {
      val expectedSuffix = "is disabled by enabledIf function in config"

      withClue("The listener should have a reason that ends with $expectedSuffix") {
         skippedListener.reasons.filter { it.endsWith(expectedSuffix) }.shouldHaveSize(1)
      }
   }
})
