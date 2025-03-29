package com.sksamuel.kotest.engine.spec.tree

import io.kotest.common.DescriptorPath
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.NotMacOnGithubCondition
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.matchers.shouldBe

// tests that behavior spec contexts are correctly nested when reporting
@EnabledIf(NotMacOnGithubCondition::class)
class BehaviorSpecTreeTest : FunSpec() {
   init {
      test("BehaviorSpec should nest tests properly") {
         val collector = CollectingTestEngineListener()
         TestEngineLauncher(collector)
            .withClasses(MyBehaviorSpecTree::class)
            .launch()
         collector.tests.mapKeys { it.key.descriptor.path() }.keys shouldBe setOf(
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- b -- c"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- b"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- d -- e"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a -- d"),
            DescriptorPath("com.sksamuel.kotest.engine.spec.tree.MyBehaviorSpecTree/a"),
         )
      }
   }
}

private class MyBehaviorSpecTree : BehaviorSpec() {
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
