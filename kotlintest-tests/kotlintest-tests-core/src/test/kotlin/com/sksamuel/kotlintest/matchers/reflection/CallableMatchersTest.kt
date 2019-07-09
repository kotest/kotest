package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import io.kotlintest.matchers.reflection.*
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
    }
  }
}