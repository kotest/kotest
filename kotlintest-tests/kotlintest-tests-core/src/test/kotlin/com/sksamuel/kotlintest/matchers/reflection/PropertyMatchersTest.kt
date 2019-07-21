package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import com.sksamuel.kotlintest.matchers.reflection.classes.SimpleItem
import io.kotlintest.matchers.reflection.*
import io.kotlintest.specs.FreeSpec

class PropertyMatchersTest : FreeSpec() {
  init {
    "should" - {
      "be of type" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldBeOfType<String>()
        }
        FancyItem::class.shouldHaveMemberProperty("value") {
          it.shouldBeOfType<Int>()
        }
      }
      "be const" {
        SimpleItem.Companion::class.shouldHaveMemberProperty("id") {
          it.shouldBeConst()
        }
      }
      "be late init" {
        FancyItem::class.shouldHaveMemberProperty("youLate") {
          it.shouldBeLateInit()
        }
      }
    }
    "should not" - {
      "be of type" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldNotBeOfType<Int>()
        }
        FancyItem::class.shouldHaveMemberProperty("value") {
          it.shouldNotBeOfType<String>()
        }
      }
      "be const" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldNotBeConst()
        }
      }
      "be late init" {
        FancyItem::class.shouldHaveMemberProperty("name") {
          it.shouldNotBeLateInit()
        }
      }
    }
  }
}