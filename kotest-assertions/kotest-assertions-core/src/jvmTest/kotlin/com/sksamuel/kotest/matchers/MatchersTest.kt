package com.sksamuel.kotest.matchers

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.types.beInstanceOf
import io.kotest.matchers.collections.haveSize
import io.kotest.matchers.or
import io.kotest.matchers.types.haveSameHashCodeAs
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.haveLength
import kotlin.collections.HashMap
import kotlin.collections.emptyMap
import kotlin.collections.mapOf
import kotlin.collections.set

class MatchersTest : FreeSpec({

   "haveSameHashCode()" {
      1 should haveSameHashCodeAs(1)
      2 shouldNot haveSameHashCodeAs(1)
   }

   "support 'or' function on matcher" {
      "hello" should (haveLength(5) or haveLength(6))
   }

   "Matchers.shouldBe" - {

      "should compare equality" {
         "a" shouldBe "a"

         shouldThrow<AssertionError> {
            "a" shouldBe "b"
         }

         123 shouldBe 123

         shouldThrow<AssertionError> {
            123 shouldBe 456
         }
      }

      "should support matching null with null" {
         val name: String? = null
         name shouldBe null
      }

      "should support matching non null with null" {
         shouldThrow<AssertionError> {
            val name: String = "nullornot"
            name shouldBe null
         }
         shouldThrow<AssertionError> {
            val name = "notnull"
            name shouldBe null
         }
      }

      "formats value representations" {

         shouldThrow<AssertionError> {
            1f shouldBe 2f
         }.message shouldBe "expected:<2.0f> but was:<1.0f>"

         shouldThrow<AssertionError> {
            1L shouldBe 2L
         }.message shouldBe "expected:<2L> but was:<1L>"

         shouldThrow<AssertionError> {
            'a' shouldBe 'b'
         }.message shouldBe "expected:<'b'> but was:<'a'>"

         shouldThrow<AssertionError> {
            "a" shouldBe "b"
         }.message shouldBe """expected:<b> but was:<a>"""
      }

      "format array errors" {
         shouldThrow<AssertionError> {
            arrayOf("a") shouldBe arrayOf("b")
         }.message shouldBe """Element differ at index: [0]
                              |expected:<["b"]> but was:<["a"]>""".trimMargin()
      }

      "format float array errors" {
         shouldThrow<AssertionError> {
            floatArrayOf(1f) shouldBe floatArrayOf(2f)
         }.message shouldBe "expected:<[2.0f]> but was:<[1.0f]>"
      }

      "format long array error" {
         shouldThrow<AssertionError> {
            longArrayOf(1L) shouldBe longArrayOf(2L)
         }.message shouldBe "expected:<[2L]> but was:<[1L]>"
      }

      "format int array error" {
         shouldThrow<AssertionError> {
            intArrayOf(1) shouldBe intArrayOf(2)
         }.message shouldBe "expected:<[2]> but was:<[1]>"
      }

      "format char array error" {
         shouldThrow<AssertionError> {
            charArrayOf('a') shouldBe charArrayOf('b')
         }.message shouldBe "expected:<['b']> but was:<['a']>"
      }

      "format byte array error" {
         shouldThrow<AssertionError> {
            byteArrayOf(1.toByte(), 35.toByte()) shouldBe byteArrayOf(12.toByte(), 13.toByte())
         }.message shouldBe "expected:<[12, 13]> but was:<[1, 35]>"
      }

      "format map error" {
         shouldThrow<AssertionError> {
            mapOf('a' to 1L) shouldBe mapOf('b' to 2L)
         }.message shouldBe "Values differed at keys a\n" +
            "expected:<{\n" +
            "  'b' = 2L\n" +
            "}> but was:<{\n" +
            "  'a' = 1L\n" +
            "}>"

         shouldThrow<AssertionError> {
            val l = HashMap<Any, Any>()
            l[1L] = l
            l shouldBe emptyMap()
         }.message shouldBe "Values differed at keys 1\n" +
            "expected:<{}> but was:<{\n" +
            "  1L = [(1L, (this HashMap))]\n" +
            "}>"
      }

      "format list error" {

         shouldThrow<AssertionError> {
            listOf('a') shouldBe listOf('b')
         }.message shouldBe """Element differ at index: [0]
                                                              |expected:<['b']> but was:<['a']>""".trimMargin()

         shouldThrow<AssertionError> {
            val l = ArrayList<Any>()
            l.add(l)
            l shouldBe emptyList()
         }.message shouldBe """Unexpected elements from index 0
                                                              |expected:<[]> but was:<[[(this Collection)]]>""".trimMargin()
      }
   }

   "Matchers.shouldNotBe" - {
      "should compare equality" {
         "a" shouldNotBe "b"
         123 shouldNotBe 456

         shouldThrow<AssertionError> {
            "a" shouldNotBe "a"
         }

         shouldThrow<AssertionError> {
            123 shouldNotBe 123
         }
      }

      "Should fail for equal primitive type" {
         shouldThrow<AssertionError> { byteArrayOf(1, 2, 3) shouldNotBe byteArrayOf(1, 2, 3) }
      }
      "should support (not) matching null with non-null" {
         "a" shouldNotBe null
      }

      "should support (not) matching non-null with null" {
         null shouldNotBe "a"
      }

      "should support (not) matching null with null" {
         shouldThrow<AssertionError> {
            null shouldNotBe null
         }
      }
   }

   "Matcher should have size x" - {
      "should compare sizes of iterables" {
         listOf(1, 2, 3) should haveSize(3)
      }
   }

   "Matchers should be an x" - {
      "should test that an instance is the required type" {
         "bibble" should beInstanceOf(String::class)
         ArrayList<String>() should beInstanceOf(List::class)
      }
   }

})
