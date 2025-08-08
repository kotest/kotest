package com.sksamuel.kotest.matchers.equality

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.*
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import kotlin.random.Random
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class ReflectionKtTest : FunSpec() {
   class HasComputedField(val name: String) { val random: Int get() = Random.nextInt()}

   data class Foo(val a: String, val b: Int, val c: Boolean)

   data class Car(val name: String, val price: Int, private val modelNumber: Int)

   class Society(val name: String, val headPerson: Person?, val hospital: Hospital)

   open class Person(val name: String) {
      var isExhausted: Boolean = false
      private var address: String = ""
      fun setAddress(newAddress: String) {
         this.address = newAddress
      }
   }

   class Teacher(
      name: String,
      val students: Array<Person> = emptyArray(),
      internal val age: Int = 123
   ): Person(name)

   class DocMetadata(val field1: String)

   class Doctor(val name: String, val age: Int, val metadata: List<DocMetadata>)

   class Hospital(val name: String, val mainDoctor: Doctor?)

   class City(val name: String, val mainHospital: Hospital)

   enum class SimpleEnum { ONE, TWO }

   enum class EnumWithProperties(val value: String) { ONE("one"), TWO("two"), }

   data class EnumWrapper<E : Enum<E>>(val enum: E)

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

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field recursively") {
         val city = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf())))
         val city2 = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf())))

         city.shouldBeEqualToComparingFields(city2)
      }

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field recursively using default shouldBe for given types") {
         val doctor = mockk<Doctor>()
         val city = City("test", Hospital("test-hospital", doctor))
         val city2 = City("test", Hospital("test-hospital", doctor))

         city.shouldBeEqualToComparingFields(
            city2,
            FieldsEqualityCheckConfig(
               useDefaultShouldBeForFields = listOf("com.sksamuel.kotest.matchers.equality.ReflectionKtTest.Doctor")
            )
         )
      }

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field recursively handling nullable fields") {
         val jasmineSociety = Society("Jasmine", Person("Andrew"), Hospital("Wellness", null))
         val roseSociety = Society("Rose", null, Hospital("Wellness", Doctor("Marco", 45, emptyList())))

         jasmineSociety.shouldNotBeEqualToComparingFields(roseSociety)
      }

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field recursively ignoring java or kotlin builtin types") {
         val city = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf(DocMetadata("f1")))))
         val city2 = City("test", Hospital("test-hospital", Doctor("doc", 51, listOf(DocMetadata("f1")))))

         city.shouldNotBeEqualToComparingFields(city2)
      }

      test("shouldBeEqualToComparingFields check equality comparing field by field including private fields") {
         val person = Person("foo")
         person.setAddress("new address")

         val errorMessage = shouldThrow<AssertionError> {
            person.shouldBeEqualToComparingFields(Person("foo"), FieldsEqualityCheckConfig(ignorePrivateFields = false))
         }.message

         errorMessage shouldContain "Using fields: address, isExhausted, name"
         errorMessage shouldContain "Value differ at:"
         errorMessage shouldContain "1) address"
         errorMessage shouldContain "expected:<<empty string>> but was:<new address>"
      }

      test("shouldBeEqualToComparingFieldsExcept check equality comparing field by field excluding given fields and private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         person.shouldBeEqualToComparingFields(
            Person("foo"),
            FieldsEqualityCheckConfig(propertiesToExclude = listOf(Person::isExhausted))
         )
         person.shouldBeEqualToComparingFields(
            Person("foo"),
            FieldsEqualityCheckConfig(
               ignorePrivateFields = true,
               propertiesToExclude = listOf(Person::isExhausted)
            )
         )
      }

      test("shouldBeEqualToComparingFieldsExcept check equality comparing field by field excluding given fields and without ignoring private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         val message = shouldThrow<AssertionError> {
            person.shouldBeEqualToComparingFields(
               Person("foo"),
               FieldsEqualityCheckConfig(
                  ignorePrivateFields = false,
                  propertiesToExclude = listOf(Person::isExhausted)
               )
            )
         }.message
         message shouldContain "Using fields: address, name"
         message shouldContain "Value differ at"
         message shouldContain "1) address"
         message shouldContain "expected:<<empty string>> but was:<new address>"

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
            Person("foo").shouldNotBeEqualToComparingFields(
               Person("foo"),
               FieldsEqualityCheckConfig(ignorePrivateFields = false)
            )
         }.message shouldContain "Using fields: address, isExhausted, name"
      }

      test("shouldBeEqualToComparingFields handles arrays") {
         val students = arrayOf(Person("foo"), Person("bar"))
         Teacher("bar", students) shouldBeEqualToComparingFields Teacher("bar", students)
      }

      test("shouldBeEqualToComparingFields can include computed field") {
         shouldFail {
            HasComputedField("foo").shouldBeEqualToComparingFields(
               HasComputedField("foo"),
               FieldsEqualityCheckConfig(ignoreComputedFields = false)
            )
         }.message shouldContain "Using fields: name, random"
      }

      test("shouldBeEqualToComparingFields includes internal fields") {
         shouldFail {
            Teacher("foo", age = 100) shouldBeEqualToComparingFields Teacher("foo", age = 200)
         }.message shouldContain "Using fields: age, isExhausted, name, students"
      }

      test("shouldBeEqualToComparingFields includes fields from superclasses") {
         shouldFail {
            Teacher("foo") shouldBeEqualToComparingFields Teacher("bar")
         }.message shouldContain "Using fields: age, isExhausted, name, students"
      }

      test("shouldBeEqualToComparingFields ignores synthetic fields") {
         shouldFail {
            HasComputedField("foo") shouldBeEqualToComparingFields HasComputedField("bar")
         }.message shouldNotContain "random"
      }

      test("shouldBeEqualToComparingFields fails if generic fields are different") {
         shouldFail {
            KeyValuePair("color", "green").shouldBeEqualToComparingFields(KeyValuePair("color", "amber"))
         }.message shouldNotContain "random"
      }

      test("shouldBeEqualToComparingFields passes if generic fields are same") {
         shouldNotThrowAny {
            KeyValuePair("color", "green").shouldBeEqualToComparingFields(KeyValuePair("color", "green"))
         }
      }

      test("shouldBeEqualToWithEnums") {
         shouldFail {
            EnumWrapper(SimpleEnum.ONE).shouldBeEqualToComparingFields(EnumWrapper(SimpleEnum.TWO))
         }.message.shouldContain("expected:<TWO> but was:<ONE>")
      }

      test("shouldBeEqualToWithEnums message contains enum names") {
         shouldFail {
            EnumWrapper(EnumWithProperties.ONE).shouldBeEqualToComparingFields(EnumWrapper(EnumWithProperties.TWO))
         }.message.shouldContain("expected:<TWO> but was:<ONE>")
      }
   }

   data class KeyValuePair<T : Any>(
      val key: String,
      val value: T
      )
}
