package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec

// tests that focus works in test factories
class TestFactoryFocusTest : FunSpec({
   include(factory)
   test("nothappy2") { error("boom") }
})

private val factory = funSpec {
   test("f:happy") {}
   test("nothappy1") { error("boom") }
}
