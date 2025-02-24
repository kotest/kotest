package io.kotest.matchers.equals

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContainInOrder

class SameInstanceTest: WordSpec() {
   init {
       "shouldBeSameInstance" should {
          "detect same instance" {
             val a = "hello"
             val b = a
             a shouldBeSameInstance b
          }
          "detect equal but different instances" {
             val a = "hello"
             val b = "hello world".substring(0, 5)
             shouldThrow<AssertionError> {
                a shouldBeSameInstance b
             }.message shouldBe """<"hello"> should be same instance as <"hello">, but the instances were equal yet different."""
          }
            "detect different instances" {
               val a = "hello"
               val b = "world"
               shouldThrow<AssertionError> {
                  a shouldBeSameInstance b
               }.message.shouldContainInOrder(
                  """<"hello"> should be same instance as <"world">""",
                  """expected:<"world"> but was:<"hello">"""
               )
            }
       }
      "shouldNotBeSameInstance" should {
         "detect same instance" {
            val a = "hello"
            val b = a
            shouldThrow<AssertionError> {
               a shouldNotBeSameInstance b
            }.message shouldBe """<"hello"> should not be same instance as <"hello">, but it was."""
         }
         "detect equal but different instances" {
            val a = "hello"
            val b = "hello world".substring(0, 5)
            shouldNotThrowAny {
               a shouldNotBeSameInstance b
            }
         }
         "detect different instances" {
            val a = "hello"
            val b = "world"
            shouldNotThrowAny {
               a shouldNotBeSameInstance b
            }
         }
      }
   }
}
