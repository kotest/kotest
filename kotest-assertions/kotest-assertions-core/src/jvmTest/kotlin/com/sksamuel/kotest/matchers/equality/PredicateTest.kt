package com.sksamuel.kotest.matchers.equality

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.FieldEqualityConfig
import io.kotest.matchers.equality.fields
import io.kotest.matchers.equality.predicates
import kotlin.random.Random

class PredicateTest : FunSpec() {

   class MyClass(val a: String) {
      private var b: String = ""
      var c: Boolean = false
      val d: Int get() = Random.nextInt()
   }

   init {
      test("should not include computed or private by default") {
         val predicates = FieldEqualityConfig().predicates()
         MyClass("foo").fields(predicates).shouldHaveSize(2)
      }

      test("allowing computed fields") {
         val config = FieldEqualityConfig()
         config.ignoreComputedFields = false
         val predicates = config.predicates()
         MyClass("foo").fields(predicates).shouldHaveSize(3)
      }

      test("allowing private fields") {
         val config = FieldEqualityConfig()
         config.ignorePrivateFields = false
         val predicates = config.predicates()
         MyClass("foo").fields(predicates).shouldHaveSize(3)
      }

      test("with included list") {
         val config = FieldEqualityConfig()
         config.ignorePrivateFields = false
         config.includedProperties = setOf(MyClass::c)
         val predicates = config.predicates()
         MyClass("foo").fields(predicates).shouldHaveSize(1)
      }

      test("with excluded list") {
         val config = FieldEqualityConfig()
         config.excludedProperties = setOf(MyClass::c)
         val predicates = config.predicates()
         MyClass("foo").fields(predicates).shouldHaveSize(1)
      }
   }
}
