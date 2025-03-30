package com.sksamuel.kotest.engine

import io.kotest.core.annotation.EnabledCondition
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.spec.Spec
import io.kotest.core.spec.style.FunSpec
import kotlin.reflect.KClass

class MySpecDisabler : EnabledCondition {
   override fun evaluate(kclass: KClass<out Spec>): Boolean {
      return false
   }
}

@EnabledIf(MySpecDisabler::class)
class MyCompleteFailureSpec : FunSpec({
   beforeSpec { throw RuntimeException() }
   afterSpec { throw RuntimeException() }

   test("Should never run") {
      throw RuntimeException()
   }
})
