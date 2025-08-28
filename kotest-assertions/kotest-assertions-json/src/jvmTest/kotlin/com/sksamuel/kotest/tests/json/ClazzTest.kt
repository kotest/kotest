package com.sksamuel.kotest.tests.json

import io.kotest.assertions.json.clazz
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDate

class ClazzTest: StringSpec() {
   init {
       "uses generic List" {
          clazz(listOf("a", "b", "c")) shouldBe List::class.java
       }
      "uses generic Map" {
         clazz(mapOf("a" to 1, "b" to 2)) shouldBe Map::class.java
      }
      "uses specific class" {
         clazz("hello") shouldBe String::class.java
         clazz(LocalDate.of(2025, 8, 28)) shouldBe LocalDate::class.java
      }
      "handles null" {
         clazz<String>(null) shouldBe Nothing::class.java
      }
   }

}
