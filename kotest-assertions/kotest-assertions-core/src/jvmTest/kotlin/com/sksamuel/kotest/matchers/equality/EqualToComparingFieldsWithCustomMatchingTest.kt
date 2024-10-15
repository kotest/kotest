package com.sksamuel.kotest.matchers.equality

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.equality.matchDoublesWithTolerance
import io.kotest.matchers.equality.matchListsIgnoringOrder
import io.kotest.matchers.equality.matchStringsIgnoringCase
import io.kotest.matchers.equality.shouldBeEqualUsingFields
import io.kotest.matchers.string.shouldContainInOrder
import java.time.LocalDateTime

class EqualToComparingFieldsWithCustomMatchingTest: StringSpec() {
   init {
       "basic example: custom matching for data class without nesting" {
          val expected = SimpleDataClass("apple", 1.0, LocalDateTime.now())
          val actual = expected.copy(weight = 1.001)
          shouldThrow<AssertionError> {
             actual shouldBeEqualUsingFields expected
          }.message.shouldContainInOrder(
             "Fields that differ:",
             "- weight  =>  expected:<1.0> but was:<1.001>",
          )
          actual shouldBeEqualUsingFields {
             overrideMatchers = mapOf(
                SimpleDataClass::weight to matchDoublesWithTolerance(0.01)
             )
             expected
          }
          shouldThrow<AssertionError> {
             actual shouldBeEqualUsingFields {
                overrideMatchers = mapOf(
                   SimpleDataClass::weight to matchDoublesWithTolerance(0.0001)
                )
                expected
             }
          }.message.shouldContainInOrder(
             "Fields that differ:",
             "- weight  =>  1.001 should be equal to 1.0 within tolerance of 1.0E-4 (lowest acceptable value is 0.9999; highest acceptable value is 1.0001)",
          )
       }
      "basic example: match name ignoring case" {
         val expected = SimpleDataClass("apple", 1.0, LocalDateTime.now())
         val actual = expected.copy(name = "Apple")
         shouldThrow<AssertionError> {
            actual shouldBeEqualUsingFields expected
         }.message.shouldContainInOrder(
            "Fields that differ:",
            """- name  =>  expected:<"apple"> but was:<"Apple">""",
         )
         actual shouldBeEqualUsingFields {
            overrideMatchers = mapOf(
               SimpleDataClass::name to matchStringsIgnoringCase
            )
            expected
         }
      }
      "basic example: match Lists ignoring order" {
         val expected = DataClassWithList("name", listOf(1, 2, 3))
         val actual = expected.copy(elements = listOf(3, 2, 1))
         shouldThrow<AssertionError> {
            actual shouldBeEqualUsingFields expected
         }.message.shouldContainInOrder(
            "Fields that differ:",
            """- elements[0]  =>  expected:<1> but was:<3>""",
            """- elements[2]  =>  expected:<3> but was:<1>""",
         )
         actual shouldBeEqualUsingFields {
            overrideMatchers = mapOf(
               DataClassWithList::elements to matchListsIgnoringOrder<Int>()
            )
            expected
         }
      }
      "Nested example: custom matching for data class with nesting" {
         val expected = NestedDataClass("name", SimpleDataClass("apple", 1.0, LocalDateTime.now()))
         val actual = expected.copy(nested = expected.nested.copy(weight = 1.001))
         shouldThrow<AssertionError> {
            actual shouldBeEqualUsingFields expected
         }.message.shouldContainInOrder(
            "Fields that differ:",
            "- nested.weight  =>  expected:<1.0> but was:<1.001>",
         )
         actual shouldBeEqualUsingFields {
            overrideMatchers = mapOf(
               SimpleDataClass::weight to matchDoublesWithTolerance(0.01)
            )
            expected
         }
         shouldThrow<AssertionError> {
            actual shouldBeEqualUsingFields {
               overrideMatchers = mapOf(
                  SimpleDataClass::weight to matchDoublesWithTolerance(0.0001)
               )
               expected
            }
         }.message.shouldContainInOrder(
            "Fields that differ:",
            "- nested.weight  =>  1.001 should be equal to 1.0 within tolerance of 1.0E-4 (lowest acceptable value is 0.9999; highest acceptable value is 1.0001)",
         )
      }
      "Nested example: custom matching of value of a Map" {
         val expected = DataClassWithMap("name",
            mapOf("key" to SimpleDataClass("apple", 1.0, LocalDateTime.now())))
         val actual = expected.copy(map = mapOf("key" to expected.map["key"]!!.copy(weight = 1.001)))
         shouldThrow<AssertionError> {
            actual shouldBeEqualUsingFields expected
         }.message.shouldContainInOrder(
            "Fields that differ:",
            "- map[key].weight  =>  expected:<1.0> but was:<1.001>",
         )
         actual shouldBeEqualUsingFields {
            overrideMatchers = mapOf(
               SimpleDataClass::weight to matchDoublesWithTolerance(0.0011)
            )
            expected
         }
      }

      "Nested example: custom matching of element of a List" {
         val expected = DataClassWithListOfDataClasses("name",
            listOf(SimpleDataClass("apple", 1.0, LocalDateTime.now()),
               SimpleDataClass("orange", 2.0, LocalDateTime.now()),
            ))
         val actual = expected.copy(elements = listOf(
            expected.elements[0].copy(weight = 1.001),
            expected.elements[1]
         ))
         shouldThrow<AssertionError> {
            actual shouldBeEqualUsingFields expected
         }.message.shouldContainInOrder(
            "Fields that differ:",
            "- elements[0].weight  =>  expected:<1.0> but was:<1.001>",
         )
         actual shouldBeEqualUsingFields {
            overrideMatchers = mapOf(
               SimpleDataClass::weight to matchDoublesWithTolerance(0.0011)
            )
            expected
         }
      }
   }

   data class SimpleDataClass(
      val name: String,
      val weight: Double,
      val createdAt: LocalDateTime
   )
   data class DataClassWithList(
      val name: String,
      val elements: List<Int>
   )
   data class NestedDataClass(
      val name: String,
      val nested: SimpleDataClass
   )
   data class DataClassWithMap(
      val name: String,
      val map: Map<String, SimpleDataClass>
   )
   data class DataClassWithListOfDataClasses(
      val name: String,
      val elements: List<SimpleDataClass>
   )
}
