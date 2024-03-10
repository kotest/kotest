package com.sksamuel.kotest.engine.spec.tree

import io.kotest.core.descriptors.TestPath
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

// tests that behavior spec contexts are correctly nested when reporting
class BehaviorSpecTreeTest : FunSpec() {
   init {
      test("BehaviorSpec should nest tests properly") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyBehaviorSpecTree::class)
            .launch()
         collector.tests.mapKeys { it.key.descriptor.path() }.keys shouldBe setOf(
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- b -- c"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- b"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- d -- e"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- d"),
            TestPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a"),
         )
      }
   }
}

class MyBehaviorSpecTree : BehaviorSpec() {
   init {
      given("a") {
         When("b") {
            then("c") {

            }
         }
         and("d") {
            then("e") {

            }
         }
      }
   }
}
