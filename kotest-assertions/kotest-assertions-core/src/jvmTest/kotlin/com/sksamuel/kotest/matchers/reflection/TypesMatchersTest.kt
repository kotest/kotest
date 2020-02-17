package com.sksamuel.kotest.matchers.reflection

import com.sksamuel.kotest.matchers.reflection.classes.FancyItem
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.reflection.shouldBeOfType
import io.kotest.matchers.reflection.shouldHaveFunction
import io.kotest.matchers.reflection.shouldNotBeOfType

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
