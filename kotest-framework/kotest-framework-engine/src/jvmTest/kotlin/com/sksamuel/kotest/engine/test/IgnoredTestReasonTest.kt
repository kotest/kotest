package com.sksamuel.kotest.engine.test

import io.kotest.core.config.ProjectConfiguration
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.descriptors.DescriptorId
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.Enabled
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

private fun CollectingTestEngineListener.getSkippedReason(name: String? = null) =
   tests.asSequence()
      .filter { if (name != null) it.key.name.testName == name else true }
      .filterNot { it.value.isSuccess }
      .single()
      .value
      .reasonOrNull

class IgnoredTestReasonTest : FunSpec() {
   init {

      test("enabledOrReasonIf should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(EnabledOrReasonIfSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "wobble"
      }

      test("EnabledExtension should report the reason for skipping") {
         val ext = object : EnabledExtension {
            override suspend fun isEnabled(descriptor: Descriptor): Enabled = if (descriptor.id != DescriptorId("pass")) {
               Enabled.disabled("wibble")
            } else {
               Enabled.enabled
            }
         }
         val c = ProjectConfiguration().apply { registry.add(ext) }
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyFunSpec::class)
            .withConfiguration(c)
            .launch()
         collector.getSkippedReason("a") shouldBe "wibble"
      }

      test("xdisabled in fun spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(XReasonFunSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "Disabled by xmethod"
      }

      test("xdisabled in describe spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(XReasonDescribeSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "Disabled by xmethod"
      }

      test("xdisabled in should spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(XReasonShouldSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "Disabled by xmethod"
      }

      test("enabled should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(EnabledSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "Disabled by enabled flag in config"
      }

      test("enabledIf should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(EnabledIfSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "Disabled by enabledIf flag in config"
      }

      test("bang should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BangSpec::class)
            .launch()
         collector.getSkippedReason("a") shouldBe "Disabled by bang"
      }
   }
}

private class BangSpec : FunSpec() {
   init {
      test("pass") { 1 shouldBe 1 }

      test("!a") {
         throw RuntimeException()
      }
   }
}

private class EnabledSpec : FunSpec() {
   init {
      test("pass") { 1 shouldBe 1 }

      test("a").config(enabled = false) {
         throw RuntimeException()
      }
   }
}

private class EnabledIfSpec : FunSpec() {
   init {
      test("pass") { 1 shouldBe 1 }

      test("a").config(enabledIf = { false }) {
         throw RuntimeException()
      }
   }
}

private class EnabledOrReasonIfSpec : FunSpec() {
   init {
      test("pass") { 1 shouldBe 1 }

      test("a").config(enabledOrReasonIf = { Enabled.disabled("wobble") }) {
         throw RuntimeException()
      }
   }
}

class XReasonFunSpec : FunSpec() {
   init {
      test("pass") { 1 shouldBe 1 }

      xtest("a") {
         throw RuntimeException()
      }
   }
}

private class XReasonDescribeSpec : DescribeSpec() {
   init {
      describe("pass") { 1 shouldBe 1 }

      xdescribe("a") {
         throw RuntimeException()
      }
   }
}

private class XReasonShouldSpec : ShouldSpec() {
   init {
      should("pass") { 1 shouldBe 1 }

      xshould("a") {
         throw RuntimeException()
      }
   }
}

private class MyFunSpec : FunSpec() {
   init {
      test("pass") { 1 shouldBe 1 }

      test("a") { }
   }
}
