package io.kotest.equals

import io.kotest.core.spec.style.FunSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class ReflectionUsingFieldsEqualityTest : FunSpec({
   data class Foo(val a: String, val b: Int, val c: Boolean)

   data class Car(val name: String, val price: Int, private val modelNumber: Int)

   test("reflection using field equality") {
      table(
         headers("actual", "expected", "ignored", "message"),
         row(
            Foo("sammy", 1, true), Foo("sammy", 1, false), listOf(Foo::a, Foo::b), """
               | Foo(a=sammy, b=1, c=false) is equal to Foo(a=sammy, b=1, c=true) by reflection equality using fields [a, b]
               | Expected: Foo(a=sammy, b=1, c=false)
               | Actual  : Foo(a=sammy, b=1, c=true)
            """.trimMargin()
         ),
         row(
            Foo("sammy", 13, true), Foo("sammy", 345435, false), listOf(Foo::a), """
               | Foo(a=sammy, b=345435, c=false) is equal to Foo(a=sammy, b=13, c=true) by reflection equality using fields [a]
               | Expected: Foo(a=sammy, b=345435, c=false)
               | Actual  : Foo(a=sammy, b=13, c=true)
            """.trimMargin()
         ),
         row(
            Foo("sammy", 13, true), Foo("sammy", 345435, true), listOf(Foo::a, Foo::c), """
               | Foo(a=sammy, b=345435, c=true) is equal to Foo(a=sammy, b=13, c=true) by reflection equality using fields [a, c]
               | Expected: Foo(a=sammy, b=345435, c=true)
               | Actual  : Foo(a=sammy, b=13, c=true)
            """.trimMargin()
         ),
         row(
            Foo("sammy", 13, true), Foo("sammy", 345435, true), listOf(Foo::c, Foo::a), """
               | Foo(a=sammy, b=345435, c=true) is equal to Foo(a=sammy, b=13, c=true) by reflection equality using fields [c, a]
               | Expected: Foo(a=sammy, b=345435, c=true)
               | Actual  : Foo(a=sammy, b=13, c=true)
            """.trimMargin()
         ),
         row(
            Foo("sammy", 42, true), Foo("sammy", 42, true), listOf(), """
               | Foo(a=sammy, b=42, c=true) is equal to Foo(a=sammy, b=42, c=true) by reflection equality using fields []
               | Expected: Foo(a=sammy, b=42, c=true)
               | Actual  : Foo(a=sammy, b=42, c=true)
            """.trimMargin()
         ),
      ).forAll { actual, expected, properties, message ->
         val result = Equality
            .byReflectionUsingFields<Foo>(*(properties.toTypedArray()))
            .verify(actual, expected)

         result.areEqual().shouldBeTrue()
         result.details().explain().shouldBe(message)
      }
   }

   test("reflection using fields failure message") {
      table(
         headers("actual", "expected", "ignored", "message"),
         row(
            Foo("sammy", 13, true),
            Foo("sammy", 345435, false),
            listOf(Foo::a, Foo::c),
            "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=sammy, b=345435, c=false) using fields [a, c]; Failed for [c: true != false]"
         ),
         row(
            Foo("sammy", 13, true),
            Foo("stef", 13, false),
            listOf(Foo::a, Foo::c),
            "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) using fields [a, c]; Failed for [a: \"sammy\" != \"stef\", c: true != false]"
         ),
      ).forAll { actual, expected, properties, message ->
         val result = Equality
            .byReflectionUsingFields<Foo>(*properties.toTypedArray())
            .verify(actual, expected)

         result.areEqual().shouldBeFalse()
         result.details().explain().shouldBe(message)
      }
   }

   test("reflection using fields should throw exception when called with properties of visibility other than public") {
      val car1 = Car("Car", 12345, 23)
      val car2 = Car("Car", 12345, 23)
      val aPrivateField = Car::class.memberProperties.find { it.visibility == KVisibility.PRIVATE }!!

      assertThrows<IllegalArgumentException>("Fields of only public visibility are allowed to be use for used for checking equality") {
         Equality
            .byReflectionUsingFields<Car>(aPrivateField)
            .verify(car1, car2)
      }
   }
})
