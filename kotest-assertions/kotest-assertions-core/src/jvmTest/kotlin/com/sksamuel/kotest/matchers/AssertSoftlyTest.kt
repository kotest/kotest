package com.sksamuel.kotest.matchers

import io.kotest.assertions.AssertionFailedError
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.errorCollector
import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.containExactly
import io.kotest.matchers.comparables.beLessThan
import io.kotest.matchers.doubles.negative
import io.kotest.matchers.doubles.positive
import io.kotest.matchers.doubles.shouldBeNegative
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeOdd
import io.kotest.matchers.ints.shouldBePositive
import io.kotest.matchers.maps.haveKey
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beEmpty
import io.kotest.matchers.string.contain
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain
import io.kotest.matchers.string.shouldNotEndWith
import io.kotest.matchers.string.shouldStartWith

class AssertSoftlyTest : FreeSpec({

   "assertSoftly" - {

      "passes when all assertions pass" {
         assertSoftly {
            1 shouldBe 1
            "foo" shouldBe "foo"
         }
      }

      "rethrows single failures" {
         val exception = shouldThrow<AssertionError> {
            assertSoftly {
               1 shouldBe 2
            }
         }
         exception.message shouldBe "expected:<2> but was:<1>"
         exception.stackTrace.first().className shouldStartWith "com.sksamuel.kotest.matchers.AssertSoftlyTest"
      }

      "groups multiple failures" {
         shouldThrow<AssertionError> {
            assertSoftly {
               1 shouldBe 2
               1 shouldBe 1 // should pass
               "foo" shouldNotBe "foo"
            }
         }.let {
            it.message should contain("1) expected:<2> but was:<1>")
            it.message should contain("2) \"foo\" should not equal \"foo\"")
            it.stackTrace.first().className shouldStartWith "com.sksamuel.kotest.matchers.AssertSoftlyTest"
         }
      }

      "works with all array types" {
         shouldThrow<AssertionError> {
            assertSoftly {
               booleanArrayOf(true) shouldBe booleanArrayOf(false)
               intArrayOf(1) shouldBe intArrayOf(2)
               shortArrayOf(1) shouldBe shortArrayOf(2)
               floatArrayOf(1f) shouldBe floatArrayOf(2f)
               doubleArrayOf(1.0) shouldBe doubleArrayOf(2.0)
               longArrayOf(1) shouldBe longArrayOf(2)
               byteArrayOf(1) shouldBe byteArrayOf(2)
               charArrayOf('a') shouldBe charArrayOf('b')
               arrayOf("foo") shouldBe arrayOf("bar")
            }
         }.let {
            it.message should contain("1) expected:<[false]> but was:<[true]>")
            it.message should contain("2) expected:<[2]> but was:<[1]>")
            it.message should contain("3) expected:<[2]> but was:<[1]>")
            it.message should contain("4) expected:<[2.0f]> but was:<[1.0f]>")
            it.message should contain("5) expected:<[2.0]> but was:<[1.0]>")
            it.message should contain("6) expected:<[2L]> but was:<[1L]>")
            it.message should contain("7) expected:<[2]> but was:<[1]>")
            it.message should contain("8) expected:<['b']> but was:<['a']>")
            it.message should contain(
               """9) Element differ at index: [0]
                                                |expected:<["bar"]> but was:<["foo"]>""".trimMargin()
            )
            it.message shouldNot contain("10) ")
         }
      }

      "works with any matcher" {
         shouldThrow<AssertionError> {
            assertSoftly {
               1 should beLessThan(0)
               "foobar" shouldNot endWith("bar")
               1 shouldBe positive() // should pass
               1.0 shouldBe negative()
               listOf(1) shouldNot containExactly(1)
               mapOf(1 to 2) should haveKey(3)
            }
         }.let {
            it.message should contain("5) Map should contain key 3")
            it.message shouldNot contain("6) ")
         }
      }

      "works with extension functions" {
         shouldThrow<AssertionError> {
            assertSoftly {
               1.shouldBeLessThan(0)
               "foobar".shouldNotEndWith("bar")
               1.shouldBePositive() // should pass
               1.0.shouldBeNegative()
            }
         }.let {
            it.message should contain("""1) 1 should be < 0""")
            it.message should contain("""2) "foobar" should not end with "bar"""")
            it.message should contain("""3) 1.0 should be < 0.0""")
            it.message shouldNot contain("4) ")
         }
      }

      "can be nested" {
         shouldThrow<AssertionError> {
            assertSoftly {
               1 shouldBe 2
               assertSoftly {
                  2 shouldBe 3
               }
            }
         }.let {
            it.message should contain("1) expected:<2> but was:<1>")
            it.message should contain("2) expected:<3> but was:<2>")
         }
      }

      "should not have any receiver context" {
         data class Person(val name: String, val age: Int)

         fun verifier(person: Person, assertion: (Person) -> Unit) {
            assertion(person)
         }

         val person = Person("foo", 0)
         verifier(person) {
            it shouldBe person
            assertSoftly {
               it shouldBe person // it being person verifies assertSoftly does not have any receiver
            }
         }
      }

      "Receiver version" - {
         "works on a receiver object" {
            var lineNumber = 0
            shouldThrow<AssertionError> {
               assertSoftly("foo") {
                  lineNumber = Thread.currentThread().stackTrace[1].lineNumber
                  length shouldBe 2
                  this[1] shouldBe 'o' // should pass
                  this shouldNotBe "foo"
               }
            }.run {
               message.shouldContainInOrder(
                  "The following 2 assertions for \"foo\" failed:",
                  "1) expected:<2> but was:<3>",
                  "AssertSoftlyTest.kt:${lineNumber + 1}",
                  "2) \"foo\" should not equal \"foo\"",
                  "AssertSoftlyTest.kt:${lineNumber + 3}",
               )
               stackTrace.first().lineNumber shouldBe lineNumber - 1
            }
         }

         "Includes the receiver in failure message when there's a single failure" {
            var lineNumber = 0
            shouldThrow<AssertionError> {
               assertSoftly("foo") {
                  lineNumber = Thread.currentThread().stackTrace[1].lineNumber
                  length shouldBe 2
               }
            }.run {
               message shouldBe """
                  The following assertion for "foo" failed:
                  expected:<2> but was:<3>
               """.trimIndent()
               stackTrace.first().lineNumber shouldBe lineNumber + 1
            }
         }

         "Returns the receiver" {
            val a = assertSoftly("foo") {
               this shouldNotBe "bar"
               shouldNotEndWith("abc")
            }

            a shouldBe "foo"
         }

         "works with 'it' receiver" {
            val a = assertSoftly("foo") {
               it shouldNotBe "bar"
            }
            a shouldBe "foo"
         }

         "works with my parameter name" {
            val a =
               assertSoftly("foo") { foo ->  // No idea why anybody would use this, but it's better to keep the verification that this works
                  foo shouldNotBe "bar"
               }

            a shouldBe "foo"
         }

         "With nested receivers" - {
            data class Person(val name: String, val age: Int)

            "Prints both subjects" {
               shouldFail {
                  assertSoftly(Person("John", 20)) {
                     name shouldBe "Jane"
                     assertSoftly(age) {
                        it shouldBeGreaterThan 30
                        it.shouldBeOdd()
                     }
                  }
               }.message.shouldContainInOrder(
                  """The following 2 assertions for Person(name=John, age=20) failed:""",
                  """1) expected:<"Jane"> but was:<"John">""",
                  """2) The following 2 assertions for 20 failed:""",
                  """   1) 20 should be > 30""",
                  """   2) 20 should be odd""",
               )
            }
         }
      }

      "Assert softly with data classes" - {
         // Added as a verification of https://github.com/kotest/kotest/issues/1831
         "work with enum in data class" {
            val source = WithSimpleEnum(enumValue = SimpleEnum.First)
            val result = WithSimpleEnum(enumValue = SimpleEnum.Second)
            val error = shouldThrow<AssertionError> {
               assertSoftly {
                  withClue("simple strings") {
                     "a" shouldBe "b"
                     "a" shouldNotBe "b"
                  }
                  withClue("more complex with data class and enums") {
                     source shouldBe result
                     source shouldNotBe result
                  }
               }
            }

            error.message shouldContain "1) simple strings\n" +
               "expected:<\"b\"> but was:<\"a\">"
            error.message shouldContain "2) data class diff for com.sksamuel.kotest.matchers.WithSimpleEnum\n" +
               "└ enumValue: more complex with data class and enums\n" +
               "expected:<Second> but was:<First>"
            error.message shouldNotContain "3) "
         }
      }
      "doesn't lose stack traces" - {
         // Added as a verification of https://github.com/kotest/kotest/issues/1831
         "single assertion failed with AssertionFailedError" {
            var lineNumber = 0
            shouldThrow<AssertionFailedError> {
               assertSoftly {
                  lineNumber = Thread.currentThread().stackTrace[1].lineNumber
                  1 shouldBe 2
               }
            }.run {
               stackTrace.first().className shouldStartWith "com.sksamuel.kotest.matchers.AssertSoftlyTest"
               stackTrace.first().lineNumber shouldBe lineNumber + 1
            }
         }
         "single assertion failed with AssertionError" {
            var lineNumber = 0
            shouldThrow<AssertionError> {
               assertSoftly {
                  lineNumber = Thread.currentThread().stackTrace[1].lineNumber
                  null should beEmpty()
               }
            }.run {
               stackTrace.first().className shouldStartWith "com.sksamuel.kotest.matchers.AssertSoftlyTest"
               stackTrace.first().lineNumber shouldBe lineNumber + 1
            }
         }
      }
   }
})

enum class SimpleEnum {
   First,
   Second
}

data class WithSimpleEnum(val enumValue: SimpleEnum = SimpleEnum.First)
