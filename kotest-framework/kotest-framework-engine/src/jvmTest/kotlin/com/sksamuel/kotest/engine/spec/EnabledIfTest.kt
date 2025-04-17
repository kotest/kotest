package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.AlwaysFalseCondition
import io.kotest.core.spec.style.FunSpec

@EnabledIf(AlwaysFalseCondition::class)
class EnabledIfTest : FunSpec() {
   init {
      // this spec should not be created
      error("boom")
   }
}

@EnabledIf(AlwaysFalseCondition::class)
open class NeverEnabledBaseSpec : FunSpec() {
   init {
      // this spec should not be created
      error("boom")
   }
}

// this spec should not be created as its parent is disabled
class InheritedEnabledIfTest : NeverEnabledBaseSpec() {
   init {
      test("whack!") {
         error("whack!")
      }
   }
}
