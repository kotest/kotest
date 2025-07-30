package com.sksamuel.kotest.property.assumptions

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.funSpec
import io.kotest.core.test.TestScope
import io.kotest.core.test.runIf
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.set
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import org.opentest4j.TestSkippedException

interface DisjointSet<out E> {
   val elements: Set<E>
   fun toSets(): Collection<Set<E>>
}

class EmptyDisjointSetImpl : DisjointSet<Nothing> {
   override val elements: Set<Nothing> get() = emptySet()
   override fun toSets(): Collection<Set<Nothing>> = emptySet()
}

class UnionFindImpl(sets: Set<Set<String>>) : DisjointSet<String> {
   override val elements: Set<Nothing> get() = emptySet()
   override fun toSets(): Collection<Set<Nothing>> = emptySet()
}

fun testDisjointSetTests(
   newDisjointSet: TestScope.(Set<Set<String>>) -> DisjointSet<String>,
) = funSpec {
   context("toSets") {
      test("should return empty set, when empty") {
         // Arrange
         val disjointSet = newDisjointSet(emptySet())

         // Act/Assert
         disjointSet.toSets() shouldBe emptySet()
      }
   }

   context("property-based tests") {
      test("all elements in toSets are contained in elements") {
         checkAll(Arb.set(Arb.set(Arb.string(1..3), 1..4), 0..5)) { sets ->
            val ds = newDisjointSet(sets)
            val allElements = ds.toSets().flatten().toSet()
            ds.elements shouldBe allElements
         }
      }
   }
}

class UnionFindImplTests : FunSpec({
   include(testDisjointSetTests { sets ->
      UnionFindImpl(sets)
   })
})

class EmptyDisjointSetImplTests : FunSpec({
   include(testDisjointSetTests { ps ->
      runIf { ps.isEmpty() }
      EmptyDisjointSetImpl()
   })
})

class CombinedSkipExceptionTest : FunSpec() {
   init {
      test("should skip when TestSkippedException is throw in a regular test") {
         throw TestSkippedException()
      }
      test("should skip when TestSkippedException is throw in an assumption") {
         
      }
   }
}
