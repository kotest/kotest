package io.kotest.assertions.arrow.core

import arrow.core.Ior
import io.kotest.core.spec.style.StringSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.string
import io.kotest.property.checkAll
import io.kotest.matchers.shouldBe

class IorMatchers : StringSpec({
  "shouldBeRight"{
    checkAll(Arb.int()) { i ->
      Ior.Right(i) shouldBeRight i
    }
  }

  "shouldNotBeRight" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Ior.Right(a) shouldNotBeRight b
    }
  }

  "shouldBeLeft"{
    checkAll(Arb.int()) { i ->
      Ior.Left(i) shouldBeLeft i
    }
  }

  "shouldNotBeLeft" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Ior.Left(a) shouldNotBeLeft b
    }
  }

  "shouldBeBoth" {
    checkAll(Arb.int(), Arb.string()) { i,j ->
      val ior = Ior.Both(i,j)
      ior.shouldBeBoth()
      ior.leftValue shouldBe i
      ior.rightValue shouldBe j
    }
  }
})
