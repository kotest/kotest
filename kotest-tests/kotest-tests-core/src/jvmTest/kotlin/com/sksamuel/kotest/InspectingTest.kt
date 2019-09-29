package com.sksamuel.kotest

import io.kotest.forOne
import io.kotest.inspecting
import io.kotest.shouldBe
import io.kotest.shouldThrowAny
import io.kotest.specs.WordSpec

class InspectingTest : WordSpec() {
  init {
    data class Person(val name: String, val age: Int, val friends: List<Person>)

    val p1 = Person("John Doe", 20, emptyList())
    val p2 = Person("Samantha Rose", 19, listOf(p1))
    val persons = listOf(p1, p2)

    "inspecting" should {
      "expose properties"{
        inspecting(p1) {
          name shouldBe "John Doe"
          age shouldBe 20
        }
      }

      "be usable within other inspectors"{
        forOne(persons) {
          inspecting(it) {
            name shouldBe "John Doe"
            age shouldBe 20
          }
        }
      }

      "be nestable"{
        inspecting(p2) {
          name shouldBe "Samantha Rose"
          age shouldBe 19
          inspecting(friends.first()) {
            name shouldBe "John Doe"
            age shouldBe 20
          }
        }
      }
      "should fail if the matchers fail"{
        shouldThrowAny {
          inspecting(p2) {
            name shouldBe "Samantha Rose"
            age shouldBe 19
            inspecting(friends.first()) {
              name shouldBe "Some name that is wrong"
              age shouldBe 19
            }
          }
        }.message shouldBe "expected: \"Some name that is wrong\" but was: \"John Doe\""
      }
    }
  }
}
