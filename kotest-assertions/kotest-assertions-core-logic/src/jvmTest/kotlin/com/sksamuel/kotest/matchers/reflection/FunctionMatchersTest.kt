package com.sksamuel.kotest.matchers.reflection

import com.sksamuel.kotest.matchers.reflection.annotations.Fancy
import com.sksamuel.kotest.matchers.reflection.classes.FancyItem
import com.sksamuel.kotest.matchers.reflection.classes.SimpleItem
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.reflection.shouldBeAnnotatedWith
import io.kotest.matchers.reflection.shouldBeInfix
import io.kotest.matchers.reflection.shouldBeInline
import io.kotest.matchers.reflection.shouldHaveAnnotations
import io.kotest.matchers.reflection.shouldHaveFunction
import io.kotest.matchers.reflection.shouldHaveReturnType
import io.kotest.matchers.reflection.shouldNotBeAnnotatedWith
import io.kotest.matchers.reflection.shouldNotBeInfix
import io.kotest.matchers.reflection.shouldNotBeInline
import io.kotest.matchers.reflection.shouldNotHaveAnnotations
import io.kotest.matchers.reflection.shouldNotHaveReturnType
import io.kotest.matchers.shouldBe

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
      "be inline" {
        SimpleItem::class.shouldHaveFunction("run") {
          it.shouldBeInline()
        }
      }
      "be infix" {
        SimpleItem::class.shouldHaveFunction("sum") {
          it.shouldBeInfix()
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
      "be inline" {
        SimpleItem::class.shouldHaveFunction("sum") {
          it.shouldNotBeInline()
        }
      }
      "be infix" {
        SimpleItem::class.shouldHaveFunction("run") {
          it.shouldNotBeInfix()
        }
      }
    }
  }
}
