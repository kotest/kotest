package com.sksamuel.kotest.property

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.data
import io.kotest.property.arbitrary.next
import kotlin.reflect.full.declaredMemberProperties

@ExperimentalStdlibApi
class ArbDataTest : FunSpec(
   {

      data class Inner(val something: Int, val somethingElse: Int)
      data class Person(val name: String, val age: Int, val isHuman: Boolean, val inner: Inner)

      test("Generate arb for data class") {
         val gen = Arb.data(Person::class)
         val person = gen.next()

         println(person)
         person shouldNotBe null
      }

      test("my test") {
         Person::class.declaredMemberProperties.forEach {
            println(it.returnType)
         }
      }

   }
)
