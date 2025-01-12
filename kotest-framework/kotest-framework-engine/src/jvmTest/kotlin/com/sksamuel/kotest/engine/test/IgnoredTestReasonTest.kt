package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.descriptors.Descriptor
import io.kotest.core.extensions.EnabledExtension
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.core.test.Enabled
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxCondition::class)
class IgnoredTestReasonTest : FunSpec() {
   init {

      test("enabledOrReasonIf should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(EnabledOrReasonIfSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "wobble"
      }

      test("EnabledExtension should report the reason for skipping") {
         val ext = object : EnabledExtension {
            override suspend fun isEnabled(descriptor: Descriptor): Enabled = Enabled.disabled("wibble")
         }
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyFunSpec::class)
            .withProjectConfig(c)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "wibble"
      }

      test("xdisabled in fun spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(XReasonFunSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "Disabled by xmethod"
      }

      test("xdisabled in describe spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(XReasonDescribeSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "Disabled by xmethod"
      }

      test("xdisabled in should spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(XReasonShouldSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "Disabled by xmethod"
      }

      test("enabled should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(EnabledSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "Disabled by enabled flag in config"
      }

      test("enabledIf should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(EnabledIfSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "Disabled by enabledIf flag in config"
      }

      test("bang should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(BangSpec::class)
            .launch()
         collector.tests.toList().first().second.reasonOrNull shouldBe "Disabled by bang"
      }
   }
}

private class BangSpec : FunSpec() {
   init {
      test("!a") {
         throw RuntimeException()
      }
   }
}

private class EnabledSpec : FunSpec() {
   init {
      test("a").config(enabled = false) {
         throw RuntimeException()
      }
   }
}

private class EnabledIfSpec : FunSpec() {
   init {
      test("a").config(enabledIf = { false }) {
         throw RuntimeException()
      }
   }
}

private class EnabledOrReasonIfSpec : FunSpec() {
   init {
      test("a").config(enabledOrReasonIf = { Enabled.disabled("wobble") }) {
         throw RuntimeException()
      }
      context("context") {
         test("a").config(enabledOrReasonIf = { Enabled.disabled("wobble") }) {
            throw RuntimeException()
         }
      }
   }
}

class XReasonFunSpec : FunSpec() {
   init {
      xtest("a") {
         throw RuntimeException()
      }
   }
}

private class XReasonDescribeSpec : DescribeSpec() {
   init {
      xdescribe("a") {
         throw RuntimeException()
      }
   }
}

private class XReasonShouldSpec : ShouldSpec() {
   init {
      xshould("a") {
         throw RuntimeException()
      }
   }
}

private class MyFunSpec : FunSpec() {
   init {
      test("a") { }
   }
}
