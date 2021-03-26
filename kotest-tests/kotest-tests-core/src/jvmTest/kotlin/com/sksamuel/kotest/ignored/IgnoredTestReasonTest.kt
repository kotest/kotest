package com.sksamuel.kotest.ignored

import io.kotest.assertions.withClue
import io.kotest.core.NamedTag
import io.kotest.core.config.ExperimentalKotest
import io.kotest.core.config.configuration
import io.kotest.core.extensions.IsActiveExtension
import io.kotest.core.listeners.TestListener
import io.kotest.core.plan.Descriptor
import io.kotest.core.spec.Isolate
import io.kotest.core.spec.Order
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.IsActive
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

private const val isActiveExtensionTestName = "is active extension should report why a test was skipped"

private var fakeRan = false
private val ignoredWithValue = "This test was ignored by enabledOrCause due to: ${UUID.randomUUID()}"
private val ignoredWithFunction = "This test was ignored by enabledOrCauseIf due to: ${UUID.randomUUID()}"
private val ignoredWithExtension = "This test was ignored by isActiveExtension due to: ${UUID.randomUUID()}"

@OptIn(ExperimentalKotest::class)
private val skippedExtension = object : IsActiveExtension {
   override suspend fun isActive(descriptor: Descriptor): IsActive =
      if (descriptor.name.value==isActiveExtensionTestName)
         IsActive.inactive(ignoredWithExtension)
      else
         IsActive.active
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

   test("expected to be ignored by enabledOrCause").config(enabledOrCause = IsActive.inactive(ignoredWithValue)) {
      throw RuntimeException()
   }

   test("expected to be ignored by enabledOrCauseIf").config(enabledOrCauseIf = { IsActive.inactive(ignoredWithFunction) }) {
      throw RuntimeException()
   }

   test("expected to be ignored by enabled").config(enabled = false) {
      throw RuntimeException()
   }

   test("expected to be ignored by enabledIf").config(enabledIf = { false }) {
      throw RuntimeException()
   }

   test(isActiveExtensionTestName) {
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
   defaultTestConfig = TestCaseConfig(enabledOrCauseIf = {
      if (fakeRan) IsActive.active else IsActive.inactive("The spec this depends on [IgnoredTestReasonFake] didn't run")
   })

   test("IgnoredTestReasonFake should have six skipped tests") {
      skippedListener.reasons.shouldHaveSize(6)
   }

   test("enabledOrCause should report the reason for skipping") {
      skippedListener.reasons.shouldContain(ignoredWithValue)
   }

   test("enabledOrCauseIf should report the reason for skipping") {
      skippedListener.reasons.shouldContain(ignoredWithFunction)
   }

   test("isActiveExtension should report the reason for skipping") {
      skippedListener.reasons.shouldContain(ignoredWithExtension)
   }

   test("xdisabled should report the reason for skipping") {
      skippedListener.reasons.shouldContain(xdisabledMessage)
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
