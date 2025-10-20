package com.sksamuel.kotest.engine.tags

import io.kotest.core.Tag
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.TestEngineLauncher
import io.kotest.engine.listener.CollectingTestEngineListener
import io.kotest.extensions.system.withSystemProperty
import io.kotest.matchers.shouldBe

class BehaviorSpecTagTest : FunSpec() {
   init {
      test("should not execute 'then' with excluded tag") {
         val listener = CollectingTestEngineListener()
         withSystemProperty("kotest.tags", "!MyTagABC") {
            TestEngineLauncher()
               .withListener(listener)
               .withClasses(ThenTagTest::class)
               .launch()
         }
         listener.tests.toList().first { it.first.name.name == "a" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "b" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "c" }.second.isIgnored shouldBe true
         listener.tests.toList().first { it.first.name.name == "d" }.second.isSuccess shouldBe true
      }

      test("when including a tag, should work at the given level") {
         val listener = CollectingTestEngineListener()
         withSystemProperty("kotest.tags", "MyTagABC") {
            TestEngineLauncher()
               .withListener(listener)
               .withClasses(GivenTagTest::class)
               .launch()
         }
         listener.tests.toList().first { it.first.name.name == "a" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "b" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "c" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "d" }.second.isIgnored shouldBe true
      }

      test("when excluding a tag, should work at the given level") {
         val listener = CollectingTestEngineListener()
         withSystemProperty("kotest.tags", "!MyTagABC") {
            TestEngineLauncher()
               .withListener(listener)
               .withClasses(GivenTagTest::class)
               .launch()
         }
         listener.tests.toList().first { it.first.name.name == "a" }.second.isIgnored shouldBe true
         listener.tests.toList().first { it.first.name.name == "d" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "e" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "f" }.second.isSuccess shouldBe true
      }

      test("when excluding a tag, should work at the when level") {
         val listener = CollectingTestEngineListener()
         withSystemProperty("kotest.tags", "!MyTagABC") {
            TestEngineLauncher()
               .withListener(listener)
               .withClasses(WhenTagTest::class)
               .launch()
         }
         listener.tests.toList().first { it.first.name.name == "a" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "b" }.second.isIgnored shouldBe true
         listener.tests.toList().first { it.first.name.name == "c" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "d" }.second.isSuccess shouldBe true
      }
   }
}

object MyTagABC : Tag()

private class GivenTagTest : BehaviorSpec({

   given("a").config(tags = setOf(MyTagABC)) {
      `when`("b") {
         then("c") {
         }
      }
   }

   given("d") {
      `when`("e") {
         then("f") {
         }
      }
   }
})

private class WhenTagTest : BehaviorSpec({

   given("a") {
      `when`("b").config(tags = setOf(MyTagABC)) {
      }
      `when`("c") {
         then("d") {
         }
      }
   }
})

private class ThenTagTest : BehaviorSpec({

   given("a") {
      `when`("b") {
         then("c").config(tags = setOf(MyTagABC)) {
         }
         then("d") {
         }
      }
   }

})

