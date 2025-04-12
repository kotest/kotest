package com.sksamuel.kotest.engine.spec.dsl.callbackorder

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.delay

// test we allow suspend functions in before / after DSL methods
@EnabledIf(LinuxOnlyGithubCondition::class)
class FunSpecBeforeAfterDslSuspendTest : FunSpec({

   var a = "foo"

   beforeTest {
      delay(100)
      a = "bar"
   }

   afterTest {
      delay(100)
   }

   test("a") {
      a shouldBe "bar"
   }
})
