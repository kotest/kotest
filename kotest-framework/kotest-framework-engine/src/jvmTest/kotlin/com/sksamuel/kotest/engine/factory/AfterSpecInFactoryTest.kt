package com.sksamuel.kotest.engine.factory

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe

/**
 * Verifies that afterSpec and beforeSpec callbacks defined inside a TestFactory are invoked
 * before/after the spec completes.
 * Regression test for https://github.com/kotest/kotest/issues/4133
 */

private var afterSpecInFactoryCount = 0

private val factoryWithAfterSpec = funSpec {
   afterSpec {
      afterSpecInFactoryCount++
   }
   test("a") {}
   test("b") {}
}

class AfterSpecInFactoryTest : FunSpec({
   include(factoryWithAfterSpec)

   afterSpec {
      afterSpecInFactoryCount shouldBe 1
   }
})

private var beforeSpecInFactoryCount = 0

private val factoryWithBeforeSpec = funSpec {
   beforeSpec {
      beforeSpecInFactoryCount++
   }
   test("a") {}
   test("b") {}
}

class BeforeSpecInFactoryTest : FunSpec({
   include(factoryWithBeforeSpec)

   afterSpec {
      beforeSpecInFactoryCount shouldBe 1
   }
})

private var factory1AfterSpecCount = 0
private var factory2AfterSpecCount = 0

private val factory1WithAfterSpec = funSpec {
   afterSpec { factory1AfterSpecCount++ }
   test("x") {}
}

private val factory2WithAfterSpec = funSpec {
   afterSpec { factory2AfterSpecCount++ }
   test("y") {}
}

class TwoFactoriesWithAfterSpecTest : FunSpec({
   include(factory1WithAfterSpec)
   include(factory2WithAfterSpec)

   afterSpec {
      factory1AfterSpecCount shouldBe 1
      factory2AfterSpecCount shouldBe 1
   }
})

private var factory1BeforeSpecCount = 0
private var factory2BeforeSpecCount = 0

private val factory1WithBeforeSpec = funSpec {
   beforeSpec { factory1BeforeSpecCount++ }
   test("x") {}
}

private val factory2WithBeforeSpec = funSpec {
   beforeSpec { factory2BeforeSpecCount++ }
   test("y") {}
}

class TwoFactoriesWithBeforeSpecTest : FunSpec({
   include(factory1WithBeforeSpec)
   include(factory2WithBeforeSpec)

   afterSpec {
      factory1BeforeSpecCount shouldBe 1
      factory2BeforeSpecCount shouldBe 1
   }
})
