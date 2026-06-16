package com.sksamuel.kotest.matchers.types

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.types.beOfType
import io.kotest.matchers.types.beTheSameInstanceAs
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.types.haveAnnotation
import io.kotest.matchers.types.instanceOf
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import io.kotest.matchers.types.shouldBeTypeOf
import io.kotest.matchers.types.shouldHaveAnnotation
import io.kotest.matchers.types.shouldNotBeInstanceOf
import io.kotest.matchers.types.shouldNotBeTypeOf
import java.util.ArrayList
import java.util.LinkedList

@Suppress("UnnecessaryVariable")
class TypeMatchersTest : WordSpec() {

  @Retention(AnnotationRetention.RUNTIME)
  annotation class Vod

  @Vod
  class Wibble

  init {

     "typeOf" should {
        "test for exact type" {
           val arrayList: List<Int> = arrayListOf(1, 2, 3)
           arrayList.shouldBeTypeOf<ArrayList<*>>()
           arrayList.shouldNotBeTypeOf<List<*>>()
        }
     }

    "haveAnnotation(annotation)" should {
      "test for the presence of an annotation" {
        Wibble::class.java should haveAnnotation(Vod::class.java)
        Wibble::class.java.shouldHaveAnnotation(Vod::class.java)
      }
    }

    "beInstanceOf" should {
       "test that value is assignable to class" {
          val arrayList: List<Int> = arrayListOf(1, 2, 3)

          arrayList should beInstanceOf(ArrayList::class)
          arrayList should beInstanceOf<ArrayList<*>>()
          arrayList should instanceOf(ArrayList::class)
          arrayList should instanceOf<ArrayList<*>>()
          arrayList.shouldBeInstanceOf<ArrayList<*>>()

          arrayList should beInstanceOf(List::class)
          arrayList should beInstanceOf<List<*>>()
          arrayList should instanceOf(List::class)
          arrayList should instanceOf<List<*>>()
          arrayList.shouldBeInstanceOf<List<*>>()

          shouldThrow<AssertionError> { arrayList should beInstanceOf(LinkedList::class) }
          shouldThrow<AssertionError> { arrayList should beInstanceOf(LinkedList::class) }
          shouldThrow<AssertionError> { arrayList should beInstanceOf<LinkedList<*>>() }
          shouldThrow<AssertionError> { arrayList should instanceOf(LinkedList::class) }
          shouldThrow<AssertionError> { arrayList should instanceOf<LinkedList<*>>() }
          shouldThrow<AssertionError> { arrayList.shouldBeInstanceOf<LinkedList<*>>() }

          arrayList.shouldNotBeInstanceOf<LinkedList<*>>()

          shouldThrow<AssertionError> { arrayList.shouldNotBeInstanceOf<ArrayList<*>>() }
       }

       "use smart contracts to cast" {
          val list: Collection<Int> = arrayListOf(1, 2, 3)
          list.shouldBeInstanceOf<ArrayList<Int>>()
          list.add(4) // this will only work if smart contracts worked
       }

       "return the receiver" {
          val list: Collection<Int> = arrayListOf(1, 2, 3)
          list.shouldBeInstanceOf<ArrayList<Int>>()
             .shouldHaveSize(3)
       }

       "Allow execution with a lambda" {
          val list = arrayListOf(1, 2, 3)

          list.shouldBeInstanceOf<ArrayList<Int>> {
             it shouldBeSameInstanceAs list
          }
       }

      "Returns typecasted value when invoked with a lambda" {
        val list = arrayListOf(1, 2, 3)

        val typecastedList = list.shouldBeInstanceOf<ArrayList<Int>> {}
        typecastedList shouldBeSameInstanceAs list
      }

      "accepts null values" {
        val arrayList: List<Int>? = null
        shouldThrow<AssertionError> { arrayList should beInstanceOf(ArrayList::class) }
        shouldThrow<AssertionError> { arrayList.shouldBeInstanceOf<ArrayList<*>>() }
        shouldThrow<AssertionError> { arrayList shouldNot beInstanceOf(List::class) }
        shouldThrow<AssertionError> { arrayList.shouldNotBeInstanceOf<LinkedList<*>>() }
      }
    }

    "beOfType" should {
      "test that value have exactly the same type" {
        val arrayList: List<Int> = arrayListOf(1, 2, 3)

        arrayList should beOfType<ArrayList<Int>>()

        shouldThrow<AssertionError> {
          arrayList should beOfType<LinkedList<Int>>()
        }

        shouldThrow<AssertionError> {
          arrayList should beOfType<List<Int>>()
        }
      }

      "Allow execution with a lambda" {
        val list: Any = arrayListOf(1, 2, 3)

        list.shouldBeTypeOf<ArrayList<Int>> {
          it shouldBeSameInstanceAs list
          it[0] shouldBe 1
        }
      }

      "Returns typecasted value when executed with a lambda" {
        val list: Any = arrayListOf(1, 2, 3)

        val typecastedList = list.shouldBeTypeOf<ArrayList<Int>> {}
        typecastedList shouldBeSameInstanceAs list
        typecastedList[0] shouldBe 1
      }

       "uses smart contracts to cast" {
          val list: Any = arrayListOf(1, 2, 3)
          list.shouldBeTypeOf<ArrayList<Int>>()
          list[0] shouldBe 1
       }

       "return the receiver" {
          arrayListOf(1, 2, 3)
             .shouldBeTypeOf<ArrayList<Int>>()
             .shouldHaveSize(3)
       }

      "accepts null values" {
        val arrayList: List<Int>? = null
        shouldThrow<AssertionError> { arrayList should beOfType<List<Int>>() }
        shouldThrow<AssertionError> { arrayList.shouldBeTypeOf<List<*>>() }
        shouldThrow<AssertionError> { arrayList shouldNot beOfType<List<Int>>() }
        shouldThrow<AssertionError> { arrayList.shouldNotBeTypeOf<List<*>>() }
      }
    }

    "TypeMatchers.theSameInstanceAs" should {
      "test that references are equal" {
        val b: List<Int>? = listOf(1, 2, 3)
        val a: List<Int>? = b
        val c: List<Int>? = listOf(1, 2, 3)

        a should beTheSameInstanceAs(b)
        a.shouldBeSameInstanceAs(b)

        shouldThrow<AssertionError> {
          a should beTheSameInstanceAs(c)
        }

        shouldThrow<AssertionError> {
          a.shouldBeSameInstanceAs(c)
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

    "beNull" should {
      val nullString: String? = null
      val nonNullString: String? = "Foo"
      "Pass for a null value" {
        nullString.shouldBeNull()
        nullString should beNull()
      }

      "Fail for a non-null value" {
        shouldThrow<AssertionError> { nonNullString.shouldBeNull() }
        shouldThrow<AssertionError> { nonNullString should beNull() }
      }
    }
  }

}
