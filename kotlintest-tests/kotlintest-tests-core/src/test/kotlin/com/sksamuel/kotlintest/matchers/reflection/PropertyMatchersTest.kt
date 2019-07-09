package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import io.kotlintest.matchers.reflection.shouldBeOfType
import io.kotlintest.matchers.reflection.shouldHaveMemberProperty
import io.kotlintest.matchers.reflection.shouldNotBeOfType
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
    }
  }
}