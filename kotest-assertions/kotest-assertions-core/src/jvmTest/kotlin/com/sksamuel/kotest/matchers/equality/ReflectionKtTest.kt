package com.sksamuel.kotest.matchers.equality

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class ReflectionKtTest : FunSpec() {

   data class Foo(val a: String, val b: Int, val c: Boolean)

   data class Car(val name: String, val price: Int, private val modelNumber: Int)

   open class Person(val name: String) {
      var isExhausted: Boolean = false
      private var address: String = ""
      fun setAddress(newAddress: String) {
         this.address = newAddress
      }
   }

   class Teacher(
      name: String,
      val students: Array<Person> = emptyArray()
   ): Person(name)

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
         }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) using fields [a, c]; Failed for [a: \"sammy\" != \"stef\", c: true != false]"
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
         }.message shouldBe "Foo(a=sammy, b=13, c=true) should be equal to Foo(a=stef, b=13, c=false) ignoring fields [c]; Failed for [a: \"sammy\" != \"stef\"]"
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

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field") {
         Person("foo") shouldBeEqualToComparingFields Person("foo")
      }

      test("shouldBeEqualToComparingFields check equality comparing field by field including private fields") {
         val person = Person("foo")
         person.setAddress("new address")

         val errorMessage = shouldThrow<AssertionError> {
            person.shouldBeEqualToComparingFields(Person("foo"), ignorePrivateFields = false)
         }.message

         errorMessage shouldContain """ Using fields: address, isExhausted, name
            | Value differ at:
            | 1) address: "new address" != <empty string>""".trimMargin()
      }

      test("shouldBeEqualToComparingFieldsExcept check equality comparing field by field excluding given fields and private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         person.shouldBeEqualToComparingFieldsExcept(
            Person("foo"),
            Person::isExhausted
         )
         person.shouldBeEqualToComparingFieldsExcept(
            Person("foo"),
            true,
            Person::isExhausted
         )
      }

      test("shouldBeEqualToComparingFieldsExcept check equality comparing field by field excluding given fields and without ignoring private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         shouldThrow<AssertionError> {
            person.shouldBeEqualToComparingFieldsExcept(
               Person("foo"),
               false,
               Person::isExhausted
            )
         }.message shouldContain """Using fields: address, name
                                   | Value differ at:
                                   | 1) address: "new address" != <empty string>""".trimMargin()
      }

      test("shouldNotBeEqualToComparingFields check all fields of expected and actual are not equal") {
         val person = Person("foo")
         person.isExhausted = true

         person shouldNotBeEqualToComparingFields Person("foo")
      }

      test("shouldNotBeEqualToComparingFields fails when expected and actual have equal fields") {
         shouldThrow<AssertionError> {
            Person("foo") shouldNotBeEqualToComparingFields Person("foo")
         }.message shouldContain "Using fields: isExhausted, name"
      }

      test("shouldNotBeEqualToComparingFields should consider private fields") {
         shouldThrow<AssertionError> {
            Person("foo").shouldNotBeEqualToComparingFields(Person("foo"), false)
         }.message shouldContain "Using fields: address, isExhausted, name"
      }

      test("shouldBeEqualToComparingFields handles arrays") {
         val students = arrayOf(Person("foo"), Person("bar"))
         Teacher("bar", students) shouldBeEqualToComparingFields Teacher("bar", students)
      }

      test("shouldBeEqualToComparingFields includes fields from superclasses") {
         shouldFail {
            Teacher("foo") shouldBeEqualToComparingFields Teacher("bar")
         }.message shouldContain "Using fields: isExhausted, name, students"
      }

      test("shouldBeEqualToComparingFields ignores synthetic fields") {
         class HasComputedField(val name: String) { val random: Int get() = Random.nextInt()}

         shouldFail {
            HasComputedField("foo") shouldBeEqualToComparingFields HasComputedField("bar")
         }.message shouldNotContain "random"
      }
   }
}
