package com.sksamuel.kotest.matchers.reflection

import com.sksamuel.kotest.matchers.reflection.classes.FancyItem
import com.sksamuel.kotest.matchers.reflection.classes.SimpleItem
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.reflection.shouldBeConst
import io.kotest.matchers.reflection.shouldBeLateInit
import io.kotest.matchers.reflection.shouldBeOfType
import io.kotest.matchers.reflection.shouldHaveMemberProperty
import io.kotest.matchers.reflection.shouldNotBeConst
import io.kotest.matchers.reflection.shouldNotBeLateInit
import io.kotest.matchers.reflection.shouldNotBeOfType

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
