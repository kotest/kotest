package com.sksamuel.kotest.engine.spec.tree

import io.kotest.core.descriptors.TestPath
import io.kotest.core.spec.style.FreeSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

// tests that free spec contexts are correctly nested when reporting
class FreeSpecTreeTest : FunSpec() {
   init {
      test("free spec should nest context's properly") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyFreeSpec::class)
            .launch()
         collector.tests.mapKeys { it.key.descriptor.path() }.keys shouldBe setOf(
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/a"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/a -- b"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/a -- b -- c"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/d"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/d -- e"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyFreeSpec/f"),
         )
      }
   }
}

class MyFreeSpec : FreeSpec() {
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
