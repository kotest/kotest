package com.sksamuel.kotest.matchers.equality

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.equality.shouldNotBeEqualToComparingFields
import io.kotest.matchers.equality.shouldNotBeEqualUsingFields
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlin.random.Random

class EqualToComparingFieldsTest : FunSpec() {

   class HasComputedField(val name: String) {
      val random: Int get() = Random.nextInt()
   }

   open class Person(val name: String) {
      var isExhausted: Boolean = false
      private var address: String = ""
      fun setAddress(newAddress: String) {
         this.address = newAddress
      }
   }

   class DocMetadata(val field1: String)

   class Teacher(
      name: String,
      val students: Array<Person> = emptyArray(),
      internal val age: Int = 123
   ) : Person(name)

   class Doctor(val name: String, val age: Int, val metadata: List<DocMetadata>)

   class Hospital(val name: String, val mainDoctor: Doctor?)

   class City(val name: String, val mainHospital: Hospital)

   class Society(val name: String, val headPerson: Person?, val hospital: Hospital)

   enum class EnumWithProperties(val value: String) { ONE("one"), TWO("two"), }

   init {

      test("shouldBeEqualUsingFields check equality comparing field by field") {
         Person("foo") shouldBeEqualUsingFields Person("foo")
      }

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field recursively") {
         val city = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf())))
         val city2 = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf())))
         city shouldBeEqualUsingFields city2
      }

      test("shouldBeEqualToComparingFieldByField check equality comparing field by field recursively using default shouldBe for given types") {

         val doctor1 = Doctor("billy", 22, emptyList())
         val doctor2 = Doctor("billy", 22, emptyList())

         val city = City("test", Hospital("test-hospital", doctor1))
         val city2 = City("test", Hospital("test-hospital", doctor2))

         city.shouldBeEqualUsingFields {
            useDefaultShouldBeForFields = listOf(Doctor::class)
            city2
         }
      }

      test("shouldNotBeEqualUsingFields check equality comparing field by field recursively handling nullable fields") {

         val jasmineSociety = Society(
            "Jasmine",
            Person("Andrew"),
            Hospital("Wellness", null)
         )

         val roseSociety = Society(
            "Rose",
            null,
            Hospital("Wellness", Doctor("Marco", 45, emptyList()))
         )

         jasmineSociety.shouldNotBeEqualUsingFields(roseSociety)
      }

      test("shouldNotBeEqualUsingFields check equality comparing field by field recursively ignoring java or kotlin builtin types") {
         val city = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf(DocMetadata("f1")))))
         val city2 = City("test", Hospital("test-hospital", Doctor("doc", 51, listOf(DocMetadata("f1")))))

         city.shouldNotBeEqualUsingFields(city2)
      }

      test("shouldBeEqualUsingFields check equality comparing field by field including private fields") {
         val person = Person("foo")
         person.setAddress("new address")

         val errorMessage = shouldThrow<AssertionError> {
            person.shouldBeEqualUsingFields {
               ignorePrivateFields = false
               Person("foo")
            }
         }.message

         errorMessage shouldContain "Using fields: address, isExhausted, name"
         errorMessage shouldContain "Value differ at:"
         errorMessage shouldContain "1) address"
         errorMessage shouldContain "expected:<<empty string>> but was:<\"new address\">"
      }

      test("shouldBeEqualUsingFields check equality comparing field by field excluding given fields and private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         person.shouldBeEqualUsingFields {
            propertiesToExclude = listOf(Person::isExhausted)
            Person("foo")
         }
         person.shouldBeEqualUsingFields {
            ignorePrivateFields = true
            propertiesToExclude = listOf(Person::isExhausted)
            Person("foo")
         }
      }

      test("shouldBeEqualUsingFields check equality comparing field by field excluding given fields and without ignoring private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         val message = shouldThrow<AssertionError> {
            person.shouldBeEqualUsingFields {
               ignorePrivateFields = false
               propertiesToExclude = listOf(Person::isExhausted)
               Person("foo")
            }
         }.message
         message shouldContain "Using fields: address, name"
         message shouldContain "Value differ at"
         message shouldContain "1) address"
         message shouldContain "expected:<<empty string>> but was:<\"new address\">"
      }

      test("shouldNotBeEqualUsingFields check all fields of expected and actual are not equal") {
         val person = Person("foo")
         person.isExhausted = true
         person shouldNotBeEqualUsingFields Person("foo")
      }

      test("shouldNotBeEqualUsingFields fails when expected and actual have equal fields") {
         shouldThrow<AssertionError> {
            Person("foo") shouldNotBeEqualUsingFields Person("foo")
         }.message shouldContain "Using fields: isExhausted, name"
      }

      test("shouldNotBeEqualUsingFields should consider private fields") {
         shouldThrow<AssertionError> {
            Person("foo").shouldNotBeEqualUsingFields {
               ignorePrivateFields = false
               Person("foo")
            }
         }.message shouldContain "Using fields: address, isExhausted, name"
      }

      test("shouldBeEqualUsingFields handles arrays") {
         val students = arrayOf(Person("foo"), Person("bar"))
         Teacher("bar", students) shouldBeEqualUsingFields Teacher("bar", students)
      }

      test("shouldBeEqualUsingFields can include computed field") {
         shouldFail {
            HasComputedField("foo").shouldBeEqualUsingFields {
               ignoreComputedFields = false
               HasComputedField("foo")
            }
         }.message shouldContain "Using fields: name, random"
      }

      test("shouldBeEqualUsingFields includes internal fields") {
         shouldFail {
            Teacher("foo", age = 100) shouldBeEqualUsingFields Teacher("foo", age = 200)
         }.message shouldContain "Using fields: age, isExhausted, name, students"
      }

      test("shouldBeEqualUsingFields includes fields from superclasses") {
         shouldFail {
            Teacher("foo") shouldBeEqualUsingFields Teacher("bar")
         }.message shouldContain "Using fields: age, isExhausted, name, students"
      }

      test("shouldBeEqualUsingFields ignores synthetic fields") {
         shouldFail {
            HasComputedField("foo") shouldBeEqualUsingFields HasComputedField("bar")
         }.message shouldNotContain "random"
      }
   }
}
