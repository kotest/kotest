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
               .withClasses(ImportantTest::class)
               .launch()
         }
         listener.tests.toList().first { it.first.name.name == "a" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "b" }.second.isSuccess shouldBe true
         listener.tests.toList().first { it.first.name.name == "c" }.second.isIgnored shouldBe true
      }
   }
}

object MyTagABC : Tag()

class ImportantTest : BehaviorSpec({

   given("a") {
      `when`("b") {
         then("c").config(tags = setOf(MyTagABC)) {
         }
      }
   }

})
