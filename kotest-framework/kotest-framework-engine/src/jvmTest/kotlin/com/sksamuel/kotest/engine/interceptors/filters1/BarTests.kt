package com.sksamuel.kotest.engine.interceptors.filters1

import io.kotest.core.spec.style.FunSpec
import com.sksamuel.kotest.engine.interceptors.testAndIncrementCounter
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition

@EnabledIf(LinuxCondition::class)
class BarTests : FunSpec({
   test("bar test a") { testAndIncrementCounter() }
   test("bar test b") { testAndIncrementCounter() }
})

