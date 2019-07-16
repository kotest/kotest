package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import io.kotlintest.matchers.reflection.shouldBeOfType
import io.kotlintest.matchers.reflection.shouldHaveFunction
import io.kotlintest.matchers.reflection.shouldNotBeOfType
import io.kotlintest.specs.FreeSpec

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