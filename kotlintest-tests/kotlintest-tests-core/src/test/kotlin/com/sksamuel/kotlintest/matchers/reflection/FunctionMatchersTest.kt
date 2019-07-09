package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.annotations.Fancy
import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import com.sksamuel.kotlintest.matchers.reflection.classes.SimpleItem
import io.kotlintest.matchers.reflection.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec

class FunctionMatchersTest : FreeSpec() {
  init {
    "should" - {
      "have annotations" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldHaveAnnotations()
          it shouldHaveAnnotations 1
        }
        SimpleItem::class.shouldHaveFunction("simpleFunction") {
          it shouldHaveAnnotations 0
        }
      }
      "have annotation" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldBeAnnotatedWith<Fancy>()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldBeAnnotatedWith<Deprecated>()
        }
      }
      "have annotation with lambda" {
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldBeAnnotatedWith<Deprecated>() {
            it.message shouldBe "Use fancyFunction instead"
          }
        }
      }
      "have return type" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldHaveReturnType<Int>()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldHaveReturnType<String>()
        }
      }
    }
    "should not" - {
      "have annotations" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it shouldNotHaveAnnotations 0
          it shouldNotHaveAnnotations 2
        }
        SimpleItem::class.shouldHaveFunction("simpleFunction") {
          it.shouldNotHaveAnnotations()
          it shouldNotHaveAnnotations 1
        }
      }
      "have annotation" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldNotBeAnnotatedWith<Deprecated>()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldNotBeAnnotatedWith<Fancy>()
        }
      }
      "have return type" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldNotHaveReturnType<String>()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldNotHaveReturnType<Int>()
        }
      }
    }
  }
}