package io.kotlintest.assertions.arrow

import arrow.core.Tuple2
import arrow.data.Validated
import arrow.instances.order
import arrow.product
import arrow.validation.refinedTypes.numeric.validated.negative.negative
import io.kotlintest.assertions.arrow.eq.shouldBeRefinedBy
import io.kotlintest.assertions.arrow.gen.gen.applicative.map
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.specs.StringSpec


@product
data class Person(val id: Long, val name: String) {
  companion object
}

fun Person.Companion.gen(): Gen<Person> =
  map(
    Gen.long(),
    Gen.string().filter { it.isNotEmpty() },
    Tuple2<Long, String>::toPerson
  )

class GenInstancesTests : StringSpec({

  "Provide a `shouldBeRefinedBy` matcher application for reified types" {
    -1 shouldBeRefinedBy Validated.negative(Int.order())
  }

  "Allow semi automatic derivation of Gen encoders for arbitrary product types" {
    forAll(Person.gen()) { it.name.isNotEmpty() }
  }

})