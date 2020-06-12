package com.sksamuel.kotest.core.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val factory = funSpec {

    // this before test should not apply to the tests in the class
    beforeTest {
        it.description.name.displayName().shouldNotBe("a")
        it.description.name.displayName().shouldBe("z")
    }

    // this after test should not apply to the tests in the class
    afterTest {
        it.a.description.name.displayName().shouldNotBe("a")
        it.a.description.name.displayName().shouldBe("z")
    }

    test("z") {

    }
}

// callbacks in a factory should only apply to tests located in that factory
class TestCallbackInFactoryIsolationTest : FunSpec({
   test("a") { }
   include(factory)
})
