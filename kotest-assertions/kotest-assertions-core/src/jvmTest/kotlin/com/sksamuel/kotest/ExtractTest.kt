package com.sksamuel.kotest

import io.kotest.assertions.extracting
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.annotation.EnabledIf
import io.kotest.core.annotation.enabledif.LinuxOnlyGithubCondition
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

@EnabledIf(LinuxOnlyGithubCondition::class)
class ExtractTest : WordSpec() {
   init {

      data class Person(val name: String, val age: Int, val friends: List<Person>)

      val p1 = Person("John Doe", 20, emptyList())
      val p2 = Person("Samantha Rose", 19, listOf(p1))
      val persons = listOf(p1, p2)

      "extracting" should {
         "extract simple properties" {
            extracting(persons) { name }
               .shouldContainAll("John Doe", "Samantha Rose")
         }

         "extract complex properties" {
            extracting(persons) { Pair(name, age) }
               .shouldContainAll(
                  Pair("John Doe", 20),
                  Pair("Samantha Rose", 19)
               )
         }
         "fail if the matcher fails" {
            shouldThrowAny {
               extracting(persons) { name }
                  .shouldContainAll("<Some name that is wrong>")
            }.message shouldBe """Collection should contain all of ["<Some name that is wrong>"] but was missing ["<Some name that is wrong>"]"""
         }

      }
   }
}
