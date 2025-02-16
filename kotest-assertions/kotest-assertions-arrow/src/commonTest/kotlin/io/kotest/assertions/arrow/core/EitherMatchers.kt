package io.kotest.assertions.arrow.core

import arrow.core.Either
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class EitherMatchers : StringSpec({
  "shouldBeRight"{
    checkAll(Arb.int()) { i ->
      Either.Right(i) shouldBeRight i
    }
  }

  "shouldNotBeRight" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Either.Right(a) shouldNotBeRight b
    }
  }

  "shouldBeLeft"{
    checkAll(Arb.int()) { i ->
      Either.Left(i) shouldBeLeft i
    }
  }

  "shouldNotBeLeft" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Either.Left(a) shouldNotBeLeft b
    }
  }
})
