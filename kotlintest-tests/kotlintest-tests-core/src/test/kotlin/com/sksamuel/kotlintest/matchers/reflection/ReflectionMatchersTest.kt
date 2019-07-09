package com.sksamuel.kotlintest.matchers.reflection

import com.sksamuel.kotlintest.matchers.reflection.annotations.Fancy
import com.sksamuel.kotlintest.matchers.reflection.classes.FancyItem
import com.sksamuel.kotlintest.matchers.reflection.classes.SimpleItem
import io.kotlintest.matchers.reflection.*
import io.kotlintest.matchers.types.shouldBeTypeOf
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.specs.FreeSpec
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

class ReflectionMatchersTest : FreeSpec() {
  init {
    "With class" - {
      "should" - {
        "have annotations" {
          FancyItem::class.shouldHaveAnnotations()
          FancyItem::class shouldHaveAnnotations 1
          SimpleItem::class shouldHaveAnnotations 0
        }
        "have annotation" {
          FancyItem::class.shouldBeAnnotatedWith<Fancy>()
        }
        "have annotation with lambda" {
          FancyItem::class.shouldBeAnnotatedWith<Fancy> {
            it.cost shouldBe 500
          }
        }
        "have function" {
          FancyItem::class shouldHaveFunction "fancyFunction"
          SimpleItem::class shouldHaveFunction "simpleFunction"
        }
        "have function with lambda" {
          FancyItem::class.shouldHaveFunction("fancyFunction") {
            it.returnType.shouldBeOfType<Int>()
          }
          FancyItem::class.shouldHaveFunction("fancyFunctionWithString") {
            it.returnType.shouldBeOfType<String>()
          }
          SimpleItem::class.shouldHaveFunction("simpleFunction") {
            it.returnType.shouldBeOfType<Int>()
          }
        }
        "have member property" {
          FancyItem::class.shouldHaveMemberProperty("name")
          FancyItem::class.shouldHaveMemberProperty("value")
          FancyItem::class.shouldHaveMemberProperty("otherField")
        }
        "have member property with lambda" {
          FancyItem::class.shouldHaveMemberProperty("name") {
            it.returnType.shouldBeOfType<String>()
          }
          FancyItem::class.shouldHaveMemberProperty("value") {
            it.returnType.shouldBeOfType<Int>()
          }
          FancyItem::class.shouldHaveMemberProperty("otherField") {
            it.returnType.shouldBeOfType<Long>()
          }
        }
      }
      "should not" - {
        "have annotations" {
          SimpleItem::class.shouldNotHaveAnnotations()
          FancyItem::class shouldNotHaveAnnotations 0
          FancyItem::class shouldNotHaveAnnotations 2
        }
        "have annotation" {
          SimpleItem::class.shouldNotBeAnnotatedWith<Fancy>()
        }
        "have function" {
          FancyItem::class shouldNotHaveFunction "foo"
          FancyItem::class shouldNotHaveFunction "bar"
          FancyItem::class shouldNotHaveFunction "simpleFunction"
          SimpleItem::class shouldNotHaveFunction "foo"
          SimpleItem::class shouldNotHaveFunction "bar"
          SimpleItem::class shouldNotHaveFunction "fancyFunction"
        }
        "have member property" {
          SimpleItem::class.shouldNotHaveMemberProperty("name")
          SimpleItem::class.shouldNotHaveMemberProperty("value")
        }
      }
    }
    "With functions" - {
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
    "With properties" - {
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
    "With callables" - {
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
    "With types" - {
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
}