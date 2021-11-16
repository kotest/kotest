package io.kotest.assertions.print

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.mpp.uniqueId

data class Foo(val name: String, val bar: Bar?, val parentFoos: List<Foo>) {
   private val id = uniqueId()

   override fun hashCode() = id.hashCode()

   override fun equals(other: Any?): Boolean {
      if (this === other) return true
      if (javaClass != other?.javaClass) return false

      other as Foo

      if (name != other.name) return false
      if (bar?.foo?.name != other.bar?.foo?.name) return false
      if (parentFoos.map { it.name } != other.parentFoos.map { it.name }) return false

      return true
   }

   override fun toString(): String {
      return "Foo(name='$name', barFooName='${bar?.foo?.name}', parentFoos=${parentFoos.map { it.name }})"
   }
}

data class Bar(val foo: Foo)

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
         val foo1OriginalParentFoos: MutableList<Foo> = mutableListOf()
         val foo1Original = Foo("foo1Original", null, foo1OriginalParentFoos)
         val foo2Original = Foo("foo2Original", Bar(foo1Original), emptyList())
         val foo3Original = Foo("foo3Original", Bar(foo1Original), emptyList())
         foo1OriginalParentFoos.addAll(listOf(foo2Original, foo3Original))

         val foo1ModifiedParentFoos: MutableList<Foo> = mutableListOf()
         val foo1Modified = Foo("foo1Modified", null, foo1ModifiedParentFoos)
         val foo2Modified = Foo("foo2Original", Bar(foo1Modified), emptyList())
         val foo3Modified = Foo("foo3Original", Bar(foo1Modified), emptyList())
         foo1ModifiedParentFoos.addAll(listOf(foo2Modified, foo3Modified))

         val one = foo1Original.print()

         val two = foo1Modified.print()

          foo1Original shouldBe foo1Modified
      }
   }
}
