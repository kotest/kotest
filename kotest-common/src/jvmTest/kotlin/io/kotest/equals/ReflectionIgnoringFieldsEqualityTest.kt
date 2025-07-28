package io.kotest.equals

import io.kotest.assertions.equals.Equality
import io.kotest.core.Tuple4
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe

class ReflectionIgnoringFieldsEqualityTest : FunSpec({
   data class Foo(val a: String, val b: Int, val c: Boolean)

   data class Car(val name: String, val price: Int, private val modelNumber: Int)

   context("equality verification ignoring fields") {
      withData(
         Tuple4(
            Foo("sammy", 1, true), Foo("sammy", 1, false), listOf(Foo::c), """
                | Foo(a=sammy, b=1, c=false) is equal to Foo(a=sammy, b=1, c=true) by reflection equality ignoring field [c] and ignoring private fields
                | Expected: Foo(a=sammy, b=1, c=false)
                | Actual  : Foo(a=sammy, b=1, c=true)
            """.trimMargin()
         ),
         Tuple4(
            Foo("sammy", 13, true), Foo("sammy", 345435, false), listOf(Foo::b, Foo::c), """
                | Foo(a=sammy, b=345435, c=false) is equal to Foo(a=sammy, b=13, c=true) by reflection equality ignoring fields [b, c] and ignoring private fields
                | Expected: Foo(a=sammy, b=345435, c=false)
                | Actual  : Foo(a=sammy, b=13, c=true)
            """.trimMargin()
         ),
         Tuple4(
            Foo("sammy", 13, true), Foo("sammy", 345435, true), listOf(Foo::b), """
                | Foo(a=sammy, b=345435, c=true) is equal to Foo(a=sammy, b=13, c=true) by reflection equality ignoring field [b] and ignoring private fields
                | Expected: Foo(a=sammy, b=345435, c=true)
                | Actual  : Foo(a=sammy, b=13, c=true)
            """.trimMargin()
         ),
      ) { (actual, expected, properties, message) ->
         val result = Equality
            .byReflectionIgnoringFields<Foo>(
               properties.first(),
               *(properties.subList(1, properties.size).toTypedArray())
            )
            .verify(actual, expected)

         result.areEqual().shouldBeTrue()
         result.details().explain().shouldBe(message)
      }
   }

   context("equality verification ignoring fields failure message") {
      withData(
         Tuple4(
            Foo("sammy", 13, true),
            Foo("sammy", 345435, false),
            listOf(Foo::a, Foo::b),
            "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=sammy, b=345435, c=false) ignoring fields [a, b]; Failed for [c: true != false]"
         ),
         Tuple4(
            Foo("sammy", 13, true),
            Foo("stef", 13, false),
            listOf(Foo::b, Foo::c),
            "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) ignoring fields [b, c]; Failed for [a: \"sammy\" != \"stef\"]"
         ),
      ) { (actual, expected, properties, message) ->
         val result = Equality
            .byReflectionIgnoringFields<Foo>(
               properties.first(),
               *(properties.subList(1, properties.size).toTypedArray())
            ).verify(actual, expected)

         result.areEqual().shouldBeFalse()
         result.details().explain().shouldBe(message)
      }
   }

   test("equality verification should ignore private fields by default") {
      val car1 = Car("C1", 10000, 430)
      val car2 = Car("C1", 123423, 123)

      Equality
         .byReflectionIgnoringFields<Car>(Car::price)
         .verify(car1, car2)
         .areEqual()
         .shouldBeTrue()
   }

   test("equality verification should consider private in equality check when ignorePrivateField is false") {
      val car1 = Car("car", 10000, 707)
      val car2 = Car("car", 9000, 700)

      Equality
         .byReflectionIgnoringFields<Car>(Car::price)
         .includingPrivateFields()
         .verify(car1, car2)
         .areEqual()
         .shouldBeFalse()
   }
})
