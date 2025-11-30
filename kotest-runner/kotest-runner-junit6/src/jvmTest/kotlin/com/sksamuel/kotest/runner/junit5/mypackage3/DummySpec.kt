package com.sksamuel.kotest.runner.junit5.mypackage3

import io.kotest.core.spec.style.FunSpec

// used for discovery filter tests
private class DummySpec5 : FunSpec({
   test("a") {}
})

internal class DummySpec6 : FunSpec({
   test("b") {}
})

class DummySpec7 : FunSpec({
   test("c") {}
})
