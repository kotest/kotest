package io.kotest.property.arrow.optics

import arrow.optics.Optional
import arrow.optics.Traversal
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.arrow.core.either
import io.kotest.property.arrow.core.functionAToB
import io.kotest.property.arrow.core.tuple9
import io.kotest.property.arrow.laws.testLaws

class TraversalTests : StringSpec({
  testLaws(
    "Traversal Laws for Optional",
    TraversalLaws.laws(
      traversal = Optional.listHead<Int>(),
      aGen = Arb.list(Arb.int()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
    ),
    TraversalLaws.laws(
      traversal = Traversal.either(),
      aGen = Arb.either(Arb.string(), Arb.int()),
      bGen = Arb.int(),
      funcGen = Arb.functionAToB(Arb.int()),
    ),
    TraversalLaws.laws(
      traversal = Traversal.pair(),
      aGen = Arb.pair(Arb.boolean(), Arb.boolean()),
      bGen = Arb.boolean(),
      funcGen = Arb.functionAToB(Arb.boolean()),
    ),
    TraversalLaws.laws(
      traversal = Traversal.tuple9(),
      aGen = Arb.tuple9(
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean(),
        Arb.boolean()
      ),
      bGen = Arb.boolean(),
      funcGen = Arb.functionAToB(Arb.boolean()),
    )
  )
})
