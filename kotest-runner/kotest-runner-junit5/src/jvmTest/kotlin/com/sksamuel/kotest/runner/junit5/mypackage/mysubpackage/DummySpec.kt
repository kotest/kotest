package com.sksamuel.kotest.runner.junit5.mypackage.mysubpackage

import io.kotest.core.spec.style.FunSpec

// used for discovery filter tests
class DummySpec1 : FunSpec({
   test("a") {}
})

class DummySpec2 : FunSpec({
   test("b") {}
})
