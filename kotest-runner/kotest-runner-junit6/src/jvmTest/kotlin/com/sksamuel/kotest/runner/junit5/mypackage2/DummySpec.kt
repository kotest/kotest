package com.sksamuel.kotest.runner.junit5.mypackage2

import io.kotest.core.spec.style.FunSpec

// used for discovery filter tests
class DummySpec3 : FunSpec({
   test("a") {}
})

class DummySpec4 : FunSpec({
   test("b") {}
})
