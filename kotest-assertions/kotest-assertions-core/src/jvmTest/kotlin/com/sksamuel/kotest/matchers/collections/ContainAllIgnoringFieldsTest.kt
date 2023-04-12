package com.sksamuel.kotest.matchers.collections

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainAllIgnoringFields
import io.kotest.matchers.throwable.shouldHaveMessage

class ContainAllIgnoringFieldsTest : WordSpec() {

   data class Person(val name: String, val age: Int, val isMember: Boolean)

   init {
      "shouldContainAllIgnoringFields" should {
         "test that a collection contains all the elements ignoring one or more fields in any order" {

            val sammy = Person("Sammy", 20, true)
            val sara = Person("Sara", 27, true)
            val jimmy = Person("Jimmy", 28, false)

            val personList = listOf(sammy, sara, jimmy)
            personList.shouldContainAllIgnoringFields(
               listOf(sammy.copy(age = 18), sara, jimmy),
               Person::name,
               Person::age
            )
            personList.shouldContainAllIgnoringFields(
               listOf(sara, sammy.copy(age = 18), jimmy),
               Person::name,
               Person::age
            )
         }

         "print missing items and ignored failed" {

            shouldThrow<AssertionError> {
               listOf(Person("Sammy", 20, true))
                  .shouldContainAllIgnoringFields(
                     listOf(Person("Jimmy", 28, true)),
                     Person::name
                  )
            }.shouldHaveMessage("""Collection should contain equals of [Person(name=Jimmy, age=28, isMember=true)] ignoring [name]""")
         }
      }
   }
}
