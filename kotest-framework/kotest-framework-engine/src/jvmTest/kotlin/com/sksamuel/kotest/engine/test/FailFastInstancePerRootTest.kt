package com.sksamuel.kotest.engine.test

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class FailFastInstancePerRootTest : FunSpec({

   test("project failfast should not skip sibling roots in InstancePerRoot mode") {
      val config = object : AbstractProjectConfig() {
         override val failfast = true
         override val isolationMode = IsolationMode.InstancePerRoot
      }

      val listener = CollectingTestEngineListener()

      TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(config)
         .withSpecRefs(SpecRef.Reference(InstancePerRootProjectFailFastFreeSpec::class))
         .execute()

      listener.result("Test a")?.isSuccess shouldBe true
      listener.result("Test b")?.isError shouldBe true
      listener.result("Test c")?.isIgnored shouldBe true

      listener.result("second test")?.isSuccess shouldBe true
      listener.result("third test")?.isSuccess shouldBe true
      listener.result("fourth test")?.isSuccess shouldBe true
   }

   test("spec failfast should not skip sibling roots in InstancePerRoot mode") {
      val config = object : AbstractProjectConfig() {
         override val isolationMode = IsolationMode.InstancePerRoot
      }

      val listener = CollectingTestEngineListener()

      TestEngineLauncher()
         .withListener(listener)
         .withProjectConfig(config)
         .withSpecRefs(SpecRef.Reference(InstancePerRootSpecFailFastFreeSpec::class))
         .execute()

      listener.result("spec Test a")?.isSuccess shouldBe true
      listener.result("spec Test b")?.isError shouldBe true
      listener.result("spec Test c")?.isIgnored shouldBe true

      listener.result("spec second test")?.isSuccess shouldBe true
      listener.result("spec third test")?.isSuccess shouldBe true
      listener.result("spec fourth test")?.isSuccess shouldBe true
   }
})

private class InstancePerRootProjectFailFastFreeSpec : FreeSpec({
   "Test root with failure test" - {
      "Test a" { }
      "Test b" { error("fail") }
      "Test c" {}
   }

   "Second root test" - {
      "second test" { }
      "third test" { }
      "fourth test" { }
   }
})

private class InstancePerRootSpecFailFastFreeSpec : FreeSpec({
   failfast = true

   "Spec root with failure test" - {
      "spec Test a" { }
      "spec Test b" { error("fail") }
      "spec Test c" {}
   }

   "Spec second root test" - {
      "spec second test" { }
      "spec third test" { }
      "spec fourth test" { }
   }
})
