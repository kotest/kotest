package io.kotest.assertions.print

import io.kotest.assertions.AssertionFailedError
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.mpp.uniqueId


class DataClassPrintTest : FunSpec() {
   init {

      test("!Detect show for data class") {
         data class Starship(
            val name: String,
            val pennnant: String,
            val displacement: Long,
            val `class`: String,
            val leadShip: Boolean
         )
         Starship("HMS Queen Elizabeth", "R08", 65000, "Queen Elizbeth", true).print() shouldBe """Starship(
- class: Queen Elizbeth
- displacement: 65000
- leadShip: true
- name: HMS Queen Elizabeth
- pennnant: R08
)"""
      }

      test("should be resilient to types with cyclic references") {
         val foo1Parent = mutableListOf<Foo>()

         val foo1 = Foo(null, foo1Parent)
         val foo2 = Foo(Bar(foo1), listOf())
         val foo3 = Foo(null, listOf())

         foo1Parent.add(foo2)

         foo1.print()
         foo2.print()

         shouldThrow<AssertionFailedError> {
            foo1 shouldBe foo3
         }
      }
   }
}

private data class Bar(val foo: Foo) {
   private val id = uniqueId()
   override fun toString() = "Bar(id=$id)"
   override fun hashCode() = id.hashCode()
   override fun equals(other: Any?) = when (other) {
      null -> false
      is Bar -> other.id == id
      else -> false
   }
}

private data class Foo(val bar: Bar?, val parents: List<Foo>) {
   private val id = uniqueId()
   override fun toString() = "Foo(id=$id)"
   override fun hashCode() = id.hashCode()
   override fun equals(other: Any?) = when (other) {
      null -> false
      is Foo -> other.id == id
      else -> false
   }
}

