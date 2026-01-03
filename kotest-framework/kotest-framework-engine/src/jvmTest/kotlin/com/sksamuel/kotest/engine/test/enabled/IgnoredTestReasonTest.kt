package com.sksamuel.kotest.engine.test.enabled

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
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

@EnabledIf(LinuxOnlyGithubCondition::class)
class IgnoredTestReasonTest : FunSpec() {
   init {

      test("enabledOrReasonIf should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(EnabledOrReasonIfSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "wobble"
      }

      test("EnabledExtension should report the reason for skipping") {
         val ext = object : EnabledExtension {
            override suspend fun isEnabled(descriptor: Descriptor): Enabled {
               return if (descriptor.id.value.contains("active")) Enabled.enabled else Enabled.disabled("wibble")
            }
         }
         val c = object : AbstractProjectConfig() {
            override val extensions = listOf(ext)
         }
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(MyFunSpec::class)
            .withProjectConfig(c)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "wibble"
      }

      test("xdisabled in fun spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(XReasonFunSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "Disabled by xmethod"
      }

      test("xdisabled in describe spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(XReasonDescribeSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "Disabled by xmethod"
      }

      test("xdisabled in should spec should report the reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(XReasonShouldSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "Disabled by xmethod"
      }

      test("enabled should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(EnabledSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "Disabled by enabled flag in config"
      }

      test("enabledIf should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(EnabledIfSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "Disabled by enabledIf flag in config"
      }

      test("bang should report some reason for skipping") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(BangSpec::class)
            .launch()
         collector.testResult("a").reasonOrNull shouldBe "Disabled by bang"
      }
   }
}

private class EnabledOrReasonIfSpec : FunSpec() {
   init {
      test("active") {}
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

private class XReasonDescribeSpec : DescribeSpec() {
   init {
      describe("active") {}
      xdescribe("a") {
         throw RuntimeException()
      }
   }
}

private class XReasonShouldSpec : ShouldSpec() {
   init {
      should("active") {}
      xshould("a") {
         throw RuntimeException()
      }
   }
}

private class XReasonFunSpec : FunSpec() {
   init {
      test("active") {}
      xtest("a") {
         throw RuntimeException()
      }
   }
}

private class MyFunSpec : FunSpec() {
   init {
      test("active") {}
      test("a") { }
   }
}

private class BangSpec : FunSpec() {
   init {
      test("active") {}
      test("!a") {
         throw RuntimeException()
      }
   }
}

private class EnabledSpec : FunSpec() {
   init {
      test("active") {}
      test("a").config(enabled = false) {
         throw RuntimeException()
      }
   }
}

private class EnabledIfSpec : FunSpec() {
   init {
      test("active") {}
      test("a").config(enabledIf = { false }) {
         throw RuntimeException()
      }
   }
}
