package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val factory = funSpec {

    // this before test should not apply to the tests in the class
    beforeTest {
        it.description.name.name.shouldNotBe("a")
        it.description.name.name.shouldBe("z")
    }

    // this after test should not apply to the tests in the class
    afterTest {
        it.a.description.name.name.shouldNotBe("a")
        it.a.description.name.name.shouldBe("z")
    }

    test("z") {

    }
}

// callbacks in a factory should only apply to tests located in that factory
class TestCallbackInFactoryIsolationTest : FunSpec({
   test("a") { }
   include(factory)
})
