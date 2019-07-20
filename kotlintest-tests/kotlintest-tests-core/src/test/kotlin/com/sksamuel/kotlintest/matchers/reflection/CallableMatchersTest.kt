package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import io.kotlintest.matchers.reflection.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.FreeSpec
import kotlin.reflect.KVisibility

class CallableMatchersTest : FreeSpec() {
  init {
    "should" - {
      "have visibility" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldHaveVisibility(KVisibility.PUBLIC)
        }
        FancyItem::class.shouldHaveMemberProperty("value") {
          it.shouldHaveVisibility(KVisibility.PROTECTED)
        }
        FancyItem::class.shouldHaveMemberProperty("otherField") {
          it.shouldHaveVisibility(KVisibility.PRIVATE)
        }
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldHaveVisibility(KVisibility.PUBLIC)
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldHaveVisibility(KVisibility.PROTECTED)
        }
      }
      "be final" {
        FancyItem::class.shouldHaveMemberProperty("otherField") {
          it.shouldBeFinal()
        }
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldBeFinal()
        }
      }
      "be open" {
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldBeOpen()
        }
      }
      "be abstract" {
        FancyItem::class.shouldHaveFunction("absFun") {
          it.shouldBeAbstract()
        }
      }
      "be suspendable" {
        FancyItem::class.shouldHaveFunction("suspendFun") {
          it.shouldBeSuspendable()
        }
      }
      "be called with" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it shouldAcceptParameters listOf(Int::class)
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it shouldAcceptParameters listOf(String::class)
        }
      }
      "be called with (with lambda)" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldAcceptParameters(listOf(Int::class)) {
            it.size shouldBe 2
          }
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldAcceptParameters(listOf(String::class)) {
            it.size shouldBe 2
          }
        }
      }
      "have parameters with name" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it shouldHaveParametersWithName listOf("fancyValue")
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it shouldHaveParametersWithName listOf("fancyStringValue")
        }
      }
      "have parameters with name (with lambda)" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldHaveParametersWithName(listOf("fancyValue")) {
            it.size shouldBe 2
          }
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldHaveParametersWithName(listOf("fancyStringValue")) {
            it.size shouldBe 2
          }
        }
      }
    }
    "should not" - {
      "have visibility" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldNotHaveVisibility(KVisibility.PROTECTED)
          it.shouldNotHaveVisibility(KVisibility.PRIVATE)
        }
        FancyItem::class.shouldHaveMemberProperty("value") {
          it.shouldNotHaveVisibility(KVisibility.PUBLIC)
          it.shouldNotHaveVisibility(KVisibility.PRIVATE)
        }
        FancyItem::class.shouldHaveMemberProperty("otherField") {
          it.shouldNotHaveVisibility(KVisibility.PUBLIC)
          it.shouldNotHaveVisibility(KVisibility.PROTECTED)
        }
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldNotHaveVisibility(KVisibility.PROTECTED)
          it.shouldNotHaveVisibility(KVisibility.PRIVATE)
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldNotHaveVisibility(KVisibility.PUBLIC)
          it.shouldNotHaveVisibility(KVisibility.PRIVATE)
        }
      }
      "be final" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldNotBeFinal()
        }
        FancyItem::class.shouldHaveMemberProperty("value") {
          it.shouldNotBeFinal()
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it.shouldNotBeFinal()
        }
      }
      "be open" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldNotBeOpen()
        }
      }
      "be abstract" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldNotBeAbstract()
        }
      }
      "be suspendable" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it.shouldNotBeSuspendable()
        }
      }
      "be called with" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it shouldNotAcceptParameters listOf(String::class)
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it shouldNotAcceptParameters listOf(Int::class)
        }
      }
      "have parameters with name" {
        FancyItem::class.shouldHaveFunction("fancyFunction") {
          it shouldNotHaveParametersWithName listOf("fancyStringValue")
        }
        FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
          it shouldNotHaveParametersWithName listOf("fancyValue")
        }
      }
    }
  }
}