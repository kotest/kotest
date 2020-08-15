package com.sksamuel.kotest.matchers.reflection

import io.kotest.core.spec.style.FreeSpec

class TypesMatchersTest : FreeSpec() {
  init {
    "should" - {
      "be of type" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.returnType.shouldBeOfType<Int>()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.returnType.shouldBeOfType<String>()
        }
      }
    }
    "should not" - {
      "be of type" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.returnType.shouldNotBeOfType<String>()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.returnType.shouldNotBeOfType<Int>()
        }
      }
    }
  }
}
