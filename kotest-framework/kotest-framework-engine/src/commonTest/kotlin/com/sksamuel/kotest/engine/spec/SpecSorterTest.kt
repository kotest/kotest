package com.sksamuel.kotest.engine.spec

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.SpecRef
import io.kotest.core.spec.style.FunSpec
import io.kotest.engine.spec.RandomSpecSorter
import io.kotest.matchers.shouldBe
import kotlin.random.Random

class SpecSorterTest : FunSpec({
   context("random spec sorter") {
      test("should not throw 'Comparison method violates its general contract' with consistent ordering") {
         val seed = 2342731194744841942L
         val specRefs: List<SpecRef> = generateSequence { SpecRef.Reference(FunSpec::class) }.take(100).toList()
         val ordered = shouldNotThrowAny { RandomSpecSorter(Random(seed)).sort(specRefs) }
         ordered shouldBe specRefs.shuffled(Random(seed))
      }
   }
})
