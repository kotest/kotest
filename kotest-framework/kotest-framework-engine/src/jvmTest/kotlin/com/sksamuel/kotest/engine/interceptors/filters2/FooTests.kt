package com.sksamuel.kotest.engine.interceptors.filters2

import com.sksamuel.kotest.engine.interceptors.testAndIncrementCounter
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxCondition
import io.kotest.core.spec.style.FunSpec

@EnabledIf(LinuxCondition::class)
class FooTests : FunSpec({
   test("foo test a") { testAndIncrementCounter() }
   test("foo test b") { testAndIncrementCounter() }
})
