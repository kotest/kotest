package io.kotest.matchers.properties

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldStartWith

class PropertiesKtTest : FunSpec({

   test("KProperty0<T>.shouldHaveValue happy path") {
      Foo("1")::a shouldHaveValue "1"
   }

   test("KProperty0<T>.shouldHaveValue with error message") {
      shouldThrowAny {
         Foo("1")::a shouldHaveValue "2"
      }.message shouldBe
         """
         Assertion failed for property 'a'
         expected:<"2"> but was:<"1">
         """.trimIndent()
   }

   test("KProperty0<T>.shouldNotHaveValue happy path") {
      Foo("1")::a shouldNotHaveValue "2"
   }

   test("KProperty0<T>.shouldNotHaveValue with error message") {
      shouldThrowAny {
         Foo("1")::a shouldNotHaveValue "1"
      }.message shouldBe
         """
         Assertion failed for property 'a'
         "1" should not equal "1"
         """.trimIndent()
   }

   test("KProperty0<T>.shouldHaveValue with clue in the error message") {
      shouldThrowAny {
         withClue("This is a clue") {
            Foo("1")::a shouldHaveValue "2"
         }
      }.message shouldBe
         """
         This is a clue
         Assertion failed for property 'a'
         expected:<"2"> but was:<"1">
         """.trimIndent()
   }

   test("KProperty0<T>.shouldHaveValue with assertSoftly happy path") {
      val value = Bar("1", 2, 3)
      assertSoftly {
         value::foo shouldHaveValue "1"
         value::bar shouldHaveValue 2
         value::bla shouldHaveValue 3
      }
   }

   test("KProperty0<T>.shouldHaveValue with assertSoftly and error message") {
      val value = Bar("1", 2, 3)
      shouldThrowAny {
         assertSoftly {
            value::foo shouldHaveValue "1"
            value::bar shouldHaveValue 1
            value::bla shouldHaveValue 2
         }
      }.message.also {
         it shouldStartWith "The following 2 assertions failed:"
         it shouldContain
            """
            1) Assertion failed for property 'bar'
            expected:<1> but was:<2>
            """.trimIndent()
         it shouldContain
            """
            2) Assertion failed for property 'bla'
            expected:<2> but was:<3>
            """.trimIndent()
      }
   }


   test("KProperty0<T>.shouldHaveValue with assertSoftly, clue and error message") {
      val value = Bar("1", 2, 3)
      shouldThrowAny {
         withClue("This is a clue") {
            assertSoftly {
               value::foo shouldHaveValue "1"
               value::bar shouldHaveValue 1
               value::bla shouldHaveValue 2
            }
         }
      }.message.also {
         it shouldStartWith "The following 2 assertions failed:"
         it shouldContain """
1) This is a clue
Assertion failed for property 'bar'
expected:<1> but was:<2>
         """.trimIndent()
         it shouldContain """
2) This is a clue
Assertion failed for property 'bla'
expected:<2> but was:<3>
         """.trimIndent()
      }
   }

})

data class Foo(val a: String)

data class Bar(val foo: String, val bar: Int, val bla: Int)
