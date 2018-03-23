package io.kotlintest.matchers

import io.kotlintest.should
import io.kotlintest.shouldThrow
import io.kotlintest.specs.AbstractWordSpec
import java.util.*

class TypeMatchersTest : AbstractWordSpec() {

  init {

    "beInstanceOf" should {
      "test that value is assignable to class" {
        val arrayList : List<Int> = arrayListOf(1,2,3)

        arrayList should beInstanceOf(ArrayList::class)

        arrayList should beInstanceOf(List::class)

        shouldThrow<AssertionError> {
          arrayList should beInstanceOf(LinkedList::class)
        }
      }
    }

    "beOfType" should {
      "test that value have exactly the same type" {
        val arrayList : List<Int> = arrayListOf(1,2,3)

        arrayList should beOfType<ArrayList<Int>>()

        shouldThrow<AssertionError> {
          arrayList should beOfType<LinkedList<Int>>()
        }

        shouldThrow<AssertionError> {
          arrayList should beOfType<List<Int>>()
        }
      }
    }

    "TypeMatchers.theSameInstanceAs" should {
      "test that references are equal" {
        val b = listOf(1, 2, 3)
        val a = b
        val c = listOf(1, 2, 3)

        a should beTheSameInstanceAs(b)

        shouldThrow<AssertionError> {
          a should beTheSameInstanceAs(c)
        }
      }
    }

    "beTheSameInstanceAs" should {
      "test that references are equal" {
        val b = listOf(1, 2, 3)
        val a = b
        val c = listOf(1, 2, 3)

        a should beTheSameInstanceAs(b)
        shouldThrow<AssertionError> {
          a should beTheSameInstanceAs(c)
        }
      }
    }
  }
}