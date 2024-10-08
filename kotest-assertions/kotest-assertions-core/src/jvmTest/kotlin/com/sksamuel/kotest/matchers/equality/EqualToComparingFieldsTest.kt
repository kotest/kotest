package com.sksamuel.kotest.matchers.equality

import io.kotest.assertions.shouldFail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.equality.shouldNotBeEqualUsingFields
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldContainInOrder
import io.kotest.matchers.string.shouldNotContain
import kotlin.random.Random

class EqualToComparingFieldsTest : FunSpec() {

   data class Foo(val a: String, val b: Int, val c: Boolean)

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

   class Box(val id: Long)
   class FooContainer(val list: List<Foo>)
   class ListContainer(val list: List<Box>)
   class MapContainer(val map: Map<String, Box>)

   data class CompletelyDifferent1(val field1: String, val field2: String)
   class CompletelyDifferent2(numberField: Int) {
      private val numberField: Int = numberField;
   }

   data class BasicDataClass(val a: String, val b: Boolean, val c: Long)

   init {

      test("check equality comparing field by field") {
         Foo("sammy", 1, true) shouldBeEqualUsingFields {
            includedProperties = setOf(Foo::a, Foo::b)
            Foo("sammy", 1, false)
         }
         Foo("sammy", 13, true) shouldBeEqualUsingFields {
            includedProperties = setOf(Foo::a)
            Foo("sammy", 345435, false)
         }
         Foo("sammy", 13, true) shouldBeEqualUsingFields {
            includedProperties = setOf(Foo::a, Foo::c)
            Foo("sammy", 345435, true)
         }
         Foo("sammy", 42, true) shouldBeEqualUsingFields {
            Foo("sammy", 42, true)
         }
      }

      test("excluded properties") {

         Foo("sammy", 1, true) shouldBeEqualUsingFields {
            excludedProperties = setOf(Foo::c)
            Foo("sammy", 1, false)
         }

         Foo("sammy", 13, true) shouldBeEqualUsingFields {
            excludedProperties = setOf(Foo::b, Foo::c)
            Foo("sammy", 345435, false)
         }
      }

      test("check equality comparing field by field recursively") {
         val city = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf())))
         val city2 = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf())))
         city shouldBeEqualUsingFields city2
      }

      test("error message for multiple failed fields") {
         shouldThrowAny {
            Foo("sammy", 1, true) shouldBeEqualUsingFields {
               Foo("terry", 2, false)
            }
         }.message shouldContain """Using fields:
 - a
 - b
 - c

Fields that differ:
 - a  =>  expected:<"terry"> but was:<"sammy">
 - b  =>  expected:<2> but was:<1>
 - c  =>  expected:<false> but was:<true>"""
      }

      test("check equality comparing field by field recursively using default shouldBe for given types") {

         val doctor1 = Doctor("billy", 22, emptyList())
         val doctor2 = Doctor("billy", 22, emptyList())

         val city = City("test", Hospital("test-hospital", doctor1))
         val city2 = City("test", Hospital("test-hospital", doctor2))

         // default shouldBe for doctor will fail as it is a not a data class
         shouldFail {
            city.shouldBeEqualUsingFields {
               useDefaultShouldBeForFields = listOf(Doctor::class)
               city2
            }
         }.message shouldContain """Using fields:
 - mainHospital.mainDoctor
 - mainHospital.name
 - name

Fields that differ:
 - mainHospital.mainDoctor  =>"""
      }

      test("error messages with nested fields") {

         val doctor1 = Doctor("billy", 23, emptyList())
         val doctor2 = Doctor("barry", 23, emptyList())

         val city = City("test1", Hospital("test-hospital1", doctor1))
         val city2 = City("test2", Hospital("test-hospital2", doctor2))

         shouldThrowAny {
            city.shouldBeEqualUsingFields {
               city2
            }
         }.message.shouldContainInOrder(
            "Using fields:",
            "- mainHospital.mainDoctor.age",
            "- mainHospital.mainDoctor.name",
            "- name",
            "Fields that differ:",
            """- mainHospital.mainDoctor.name  =>  expected:<"barry"> but was:<"billy">""",
            "- mainHospital.name  =>",
            """expected:<"test-hospital2"> but was:<"test-hospital1">""",
            """- name  =>  expected:<"test2"> but was:<"test1">""",
         )
      }

      test("check equality comparing field by field recursively handling nullable fields") {

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

      test("check equality comparing field by field recursively ignoring java or kotlin builtin types") {
         val city = City("test", Hospital("test-hospital", Doctor("doc", 50, listOf(DocMetadata("f1")))))
         val city2 = City("test", Hospital("test-hospital", Doctor("doc", 51, listOf(DocMetadata("f1")))))

         city.shouldNotBeEqualUsingFields(city2)
      }

      test("check equality comparing field by field including private fields") {
         val person = Person("foo")
         person.setAddress("new address")

         val errorMessage = shouldThrow<AssertionError> {
            person.shouldBeEqualUsingFields {
               ignorePrivateFields = false
               Person("foo")
            }
         }.message

         errorMessage shouldContain """Using fields:
 - address
 - isExhausted
 - name

Fields that differ:
 - address  =>  expected:<<empty string>> but was:<"new address">"""
      }

      test("check equality comparing field by field excluding given fields and private fields") {
         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         person.shouldBeEqualUsingFields {
            excludedProperties = listOf(Person::isExhausted)
            Person("foo")
         }
         person.shouldBeEqualUsingFields {
            ignorePrivateFields = true
            excludedProperties = listOf(Person::isExhausted)
            Person("foo")
         }
      }

      test("should ignore private fields by default") {
         val person = Person("foo")
         person.setAddress("new address")

         person.shouldBeEqualUsingFields {
            Person("foo")
         }
      }

      test("should consider private fields when enabled") {

         val person = Person("foo")
         person.isExhausted = true
         person.setAddress("new address")

         val person2 = Person("foo")
         person2.isExhausted = false
         person2.setAddress("new address")

         shouldThrow<AssertionError> {
            person.shouldBeEqualUsingFields {
               ignorePrivateFields = false
               person2
            }
         }.message shouldContain """Using fields:
 - address
 - isExhausted
 - name

Fields that differ:
 - isExhausted  =>  expected:<false> but was:<true>"""
      }

      test("shouldNotBeEqualUsingFields check all fields of expected and actual are not equal") {
         val person = Person("foo")
         person.isExhausted = true
         person shouldNotBeEqualUsingFields Person("foo")
      }

      test("shouldNotBeEqualUsingFields fails when expected and actual have equal fields") {
         shouldThrow<AssertionError> {
            Person("foo") shouldNotBeEqualUsingFields Person("foo")
         }.message shouldContain """Using fields:
 - isExhausted
 - name"""
      }

      test("handles arrays") {
         val students = arrayOf(Person("foo"), Person("bar"))
         Teacher("bar", students) shouldBeEqualUsingFields Teacher("bar", students)
      }

      test("using included properties setting") {
         BasicDataClass("foo", true, 2) shouldBeEqualUsingFields {
            includedProperties = listOf(BasicDataClass::a)
            BasicDataClass("foo", false, 3)
         }
      }

      test("using excluded properties setting") {
         BasicDataClass("foo", true, 1) shouldBeEqualUsingFields {
            excludedProperties = listOf(BasicDataClass::a)
            BasicDataClass("bar", true, 1)
         }
      }

      test("can include computed fields") {
         shouldFail {
            HasComputedField("foo").shouldBeEqualUsingFields {
               ignoreComputedFields = false
               HasComputedField("foo")
            }
         }.message shouldContain """Using fields:
 - name
 - random

Fields that differ:
 - random  =>  """
      }

      test("ignore computed fields by default") {
         shouldFail {
            HasComputedField("foo") shouldBeEqualUsingFields HasComputedField("bar")
         }.message shouldNotContain "random"
      }

      test("include internal fields") {
         shouldFail {
            Teacher("foo", age = 100) shouldBeEqualUsingFields Teacher("foo", age = 200)
         }.message shouldContain """Using fields:
 - age
 - students
 - isExhausted
 - name

Fields that differ:
 - age  =>  expected:<200> but was:<100>"""
      }

      test("include fields from superclasses") {
         shouldFail {
            Teacher("foo") shouldBeEqualUsingFields Teacher("bar")
         }.message shouldContain """Using fields:
 - age
 - students
 - isExhausted
 - name

Fields that differ:
 - name  =>  expected:<"bar"> but was:<"foo">"""
      }

      test("should compare lists") {
         val a = ListContainer(listOf(Box(0)))
         val b = ListContainer(listOf(Box(0)))
         a shouldBeEqualUsingFields b
      }

      test("should compare lists using shouldBe for elements") {
         val a = FooContainer(listOf(Foo("a", 1, true)))
         val b = FooContainer(listOf(Foo("a", 1, true)))
         a shouldBeEqualUsingFields {
            useDefaultShouldBeForFields = setOf(Foo::class)
            b
         }
      }

      test("should fail on different list elements") {
         val a = ListContainer(listOf(Box(0)))
         val b = ListContainer(listOf(Box(1)))
         shouldFail {
            a shouldBeEqualUsingFields b
         }.message shouldContain """Using fields:
 - list[0].id

Fields that differ:
 - list[0].id  =>  expected:<1L> but was:<0L>"""
      }

      test("should pass on empty lists") {
         val a = ListContainer(emptyList())
         val b = ListContainer(emptyList())
         a shouldBeEqualUsingFields b
      }

      test("should fail on different length lists") {
         val a = ListContainer(listOf(Box(0)))
         val b = ListContainer(listOf(Box(0), Box(0)))
         shouldThrow<java.lang.AssertionError> {
            a shouldBeEqualUsingFields b
         }
      }

      test("should compare maps") {
         val a = MapContainer(mapOf("foo" to Box(0)))
         val b = MapContainer(mapOf("foo" to Box(0)))
         a shouldBeEqualUsingFields b
      }

      test("should fail on different map elements") {
         val a = MapContainer(mapOf("foo" to Box(0)))
         val b = MapContainer(mapOf("foo" to Box(1)))
         shouldFail {
            a shouldBeEqualUsingFields b
         }.message shouldContain """Using fields:
 - map[foo].id

Fields that differ:
 - map[foo].id  =>  expected:<1L> but was:<0L>"""
      }

      test("should fail on different map keys") {
         val a = MapContainer(mapOf("foo" to Box(0)))
         val b = MapContainer(mapOf("bar" to Box(0)))
         shouldFail {
            a shouldBeEqualUsingFields b
         }.message shouldContain """Using fields:
 - map[foo]

Fields that differ:
 - map[foo]  =>  Expected null but actual was"""
      }

      test("should pass on empty maps") {
         val a = MapContainer(emptyMap())
         val b = MapContainer(emptyMap())
         a shouldBeEqualUsingFields b
      }

      test("should fail on different map lengths") {
         val a = MapContainer(mapOf("foo" to Box(0)))
         val b = MapContainer(mapOf("foo" to Box(0), "bar" to Box(1)))
         shouldThrow<java.lang.AssertionError> {
            a shouldBeEqualUsingFields b
         }
      }

      test("should check property lists are equal") {
         val a = CompletelyDifferent2(1)
         val b = CompletelyDifferent1("a", "b")
         shouldFail {
            a shouldBeEqualUsingFields {
               excludedProperties = listOf(CompletelyDifferent1::field1)
               b
            }
         }.message shouldContain """with mismatched properties"""
      }

      test("setting both include and excluded property lists should error") {
         shouldThrowAny {
            BasicDataClass("foo", true, 1) shouldBeEqualUsingFields {
               includedProperties = listOf(BasicDataClass::a)
               excludedProperties = listOf(BasicDataClass::b)
               BasicDataClass("foo", true, 1)
            }
         }
      }
   }
}
