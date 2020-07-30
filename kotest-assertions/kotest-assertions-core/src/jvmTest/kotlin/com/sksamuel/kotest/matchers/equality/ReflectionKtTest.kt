package com.sksamuel.kotest.matchers.equality

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualToIgnoringFields
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.kotest.matchers.equality.shouldNotBeEqualToIgnoringFields
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.assertThrows
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class ReflectionKtTest : FunSpec() {

   data class Foo(val a: String, val b: Int, val c: Boolean)

   data class Car(val name: String, val price: Int, private val modelNumber: Int)

   init {

      test("shouldBeEqualToUsingFields") {
         Foo("sammy", 1, true).shouldBeEqualToUsingFields(Foo("sammy", 1, false), Foo::a, Foo::b)
         Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, false), Foo::a)
         Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, true), Foo::a, Foo::c)
         Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, true), Foo::c, Foo::a)
         Foo("sammy", 42, true).shouldBeEqualToUsingFields(Foo("sammy", 42, true))
      }

      test("shouldBeEqualToUsingFields failure message") {

         shouldThrow<AssertionError> {
            Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("sammy", 345435, false), Foo::a, Foo::c)
         }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=sammy, b=345435, c=false) using fields [a, c]; Failed for [c: true != false]"

         shouldThrow<AssertionError> {
            Foo("sammy", 13, true).shouldBeEqualToUsingFields(Foo("stef", 13, false), Foo::a, Foo::c)
         }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) using fields [a, c]; Failed for [a: sammy != stef, c: true != false]"
      }

      test("shouldBeEqualToIgnoringFields") {
         Foo("sammy", 1, true).shouldBeEqualToIgnoringFields(Foo("sammy", 1, false), Foo::c)
         Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("sammy", 345435, false), Foo::b, Foo::c)
         Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("sammy", 345435, true), Foo::b)
      }

      test("shouldBeEqualToIgnoringFields failure message") {

         shouldThrow<AssertionError> {
            Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("sammy", 345435, false), Foo::a, Foo::b)
         }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=sammy, b=345435, c=false) ignoring fields [a, b]; Failed for [c: true != false]"

         shouldThrow<AssertionError> {
            Foo("sammy", 13, true).shouldBeEqualToIgnoringFields(Foo("stef", 13, false), Foo::c)
         }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) ignoring fields [c]; Failed for [a: sammy != stef]"
      }

      test("shouldBeEqualToIgnoringFields should throw exception when no field is mentioned") {
         assertThrows<IllegalArgumentException>("At-least one field is required to be mentioned to be ignore for checking the equality") {
            Foo("sammy", 23, false).shouldBeEqualToIgnoringFields(Foo("danny", 23, false))
         }
      }

      test("shouldBeEqualToIgnoringFields should compare equality for class having private fields") {
         val car1 = Car("C1", 10000, 430)
         val car2 = Car("C1", 123423, 123)

         car2.shouldBeEqualToIgnoringFields(car1, Car::price)
      }

      test("shouldBeEqualToUsingFields should throw exception when called with properties of visibility other than public") {
         val car1 = Car("Car", 12345, 23)
         val car2 = Car("Car", 12345, 23)
         val aPrivateField = Car::class.memberProperties.find { it.visibility == KVisibility.PRIVATE }!!

         assertThrows<IllegalArgumentException>("Fields of only public visibility are allowed to be use for used for checking equality") {
            car1.shouldBeEqualToUsingFields(car2, aPrivateField)
         }
      }

      test("shouldBeEqualToIgnoringFields should consider private in equality check when ignorePrivateField is false") {
         val car1 = Car("car", 10000, 707)
         val car2 = Car("car", 9000, 700)
         val car3 = Car("car", 7000, 707)

         car1.shouldBeEqualToIgnoringFields(car3, false, Car::price)
         shouldThrow<AssertionError> {
            car1.shouldBeEqualToIgnoringFields(car2, false, Car::price)
         }
      }

      test("shouldNotBeEqualToIgnoringFields should consider private in equality check when ignorePrivateField is false") {
         val car1 = Car("car", 10000, 707)
         val car2 = Car("car", 9000, 700)
         val car3 = Car("car", 7000, 707)

         car1.shouldNotBeEqualToIgnoringFields(car2, false, Car::price)
         shouldThrow<AssertionError> {
            car1.shouldNotBeEqualToIgnoringFields(car3, false, Car::price)
         }
      }

      test("shouldBeEqualToIgnoringFields should not consider private in equality check when ignorePrivateField is true") {
         val car1 = Car("car", 10000, 707)
         val car2 = Car("car", 9000, 700)

         car1.shouldBeEqualToIgnoringFields(car2, true, Car::price)
      }
   }
}
