package com.sksamuel.kotest.engine.spec

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import kotlin.reflect.KClass

class NeverEnabled : EnabledCondition {
   override fun enabled(kclass: KClass<out Spec>): Boolean = false
}

@EnabledIf(NeverEnabled::class)
class EnabledIfTest : FunSpec() {
   init {
      // this spec should not be created
      error("boom")
   }
}
