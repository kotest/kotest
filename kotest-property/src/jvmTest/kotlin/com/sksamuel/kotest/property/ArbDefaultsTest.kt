package com.sksamuel.kotest.property

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.WordSpec
import io.kotest.inspectors.forAll
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.throwable.shouldHaveMessage
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.property.Arb
import io.kotest.property.checkAll
import io.kotest.property.resolution.default

class ArbDefaultsTest : WordSpec({

   "Gen.default" should {
      "generate the defaults for list" {

         val gen = Arb.default<List<Int>>()
         checkAll(10, gen) { list ->
            list.forAll { i ->
               i.shouldBeInstanceOf<Int>()
            }
         }
      }

      "generate the defaults for set" {

         val gen = Arb.default<Set<String>>()
         checkAll(10, gen) { inst ->
            inst.forAll { i ->
               i.shouldBeInstanceOf<String>()
            }
         }
      }

      "support basic data classes" {
         checkAll<Foo> { it.shouldNotBeNull() }
      }

      "throw on complex data class" {
         shouldThrow<IllegalStateException> {
            checkAll<Bar> { it.shouldNotBeNull() }
         }.shouldHaveMessage("Failed to create generator for parameter com.sksamuel.kotest.property.Bar.t")
      }

      "throw for nested parameters" {
         val e = shouldThrow<IllegalStateException> {
            checkAll<Ear> { it.shouldNotBeNull() }
         }
         e.shouldHaveMessage("Failed to create generator for parameter com.sksamuel.kotest.property.Ear.d")
         e.cause!!.cause!!.cause!!.shouldHaveMessage("Failed to create generator for parameter com.sksamuel.kotest.property.Bar.t")
      }
   }
})

data class Foo(val s: String, val b: Boolean, val i: Int, val d: Double, val f: Float, val l: Long)

data class Bar(val s: String, val t: Thread)
data class Car(val b: Bar)
data class Dar(val c: Car)
data class Ear(val d: Dar)
