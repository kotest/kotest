package com.sksamuel.kotest.engine.spec.tree

import io.kotest.core.descriptors.DescriptorPath
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

// tests that free spec contexts are correctly nested when reporting
@EnabledIf(LinuxOnlyGithubCondition::class)
class FreeSpecTreeTest : FunSpec() {
   init {
      test("free spec should nest context's properly") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher().withListener(collector)
            .withClasses(MyFreeSpec::class)
            .launch()
         collector.tests.mapKeys { it.key.descriptor.path() }.keys shouldBe setOf(
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/a"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/a -- b"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/a -- b -- c"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/d"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/d -- e"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/f"),
         )
      }
   }
}

private class MyFreeSpec : FreeSpec() {
   init {
      "a" - {
         "b" - {
            "c" {
            }
         }
      }
      "d" - {
         "e" {
         }
      }
      "f" {
      }
   }
}
