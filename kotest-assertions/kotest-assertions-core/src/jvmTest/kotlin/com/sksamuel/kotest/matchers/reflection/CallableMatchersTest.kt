package com.sksamuel.kotest.matchers.reflection

import com.sksamuel.kotest.matchers.reflection.classes.FancyItem
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.reflection.shouldAcceptParameters
import io.kotest.matchers.reflection.shouldBeAbstract
import io.kotest.matchers.reflection.shouldBeFinal
import io.kotest.matchers.reflection.shouldBeOpen
import io.kotest.matchers.reflection.shouldBeSuspendable
import io.kotest.matchers.reflection.shouldHaveFunction
import io.kotest.matchers.reflection.shouldHaveMemberProperty
import io.kotest.matchers.reflection.shouldHaveParametersWithName
import io.kotest.matchers.reflection.shouldHaveVisibility
import io.kotest.matchers.reflection.shouldNotAcceptParameters
import io.kotest.matchers.reflection.shouldNotBeAbstract
import io.kotest.matchers.reflection.shouldNotBeFinal
import io.kotest.matchers.reflection.shouldNotBeOpen
import io.kotest.matchers.reflection.shouldNotBeSuspendable
import io.kotest.matchers.reflection.shouldNotHaveParametersWithName
import io.kotest.matchers.reflection.shouldNotHaveVisibility
import io.kotest.matchers.shouldBe
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
