package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

private val factory = funSpec {

    // this before test should not apply to the tests in the class
    beforeTest {
        it.descriptor.id.value.shouldNotBe("a")
        it.descriptor.id.value.shouldBe("z")
    }

    // this after test should not apply to the tests in the class
    afterTest {
        it.a.descriptor.id.value.shouldNotBe("a")
        it.a.descriptor.id.value.shouldBe("z")
    }

    test("z") {

    }
}

// callbacks in a factory should only apply to test located in that factory
class TestCallbackInFactoryIsolationTest : FunSpec({
   test("a") { }
   include(factory)
})
