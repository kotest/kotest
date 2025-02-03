package io.kotest.assertions.arrow.core

import arrow.core.Option
import arrow.core.Some
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.checkAll

class OptionMatchers : StringSpec({
  "Option.shouldBeSome"  {
    checkAll(Arb.int()) { i ->
      Option(i) shouldBeSome i
    }
  }

  "shouldNotBeSome" {
    checkAll(
      Arb.bind(Arb.int(), Arb.int(), ::Pair)
        .filter { (a, b) -> a != b }
    ) { (a, b) ->
      Some(a) shouldNotBeSome b
    }
  }

  "Option shouldBe some(value)"  {
    shouldThrow<AssertionError> {
      Option.fromNullable<String>(null) shouldBeSome "foo"
    }.message shouldBe "Expected Some, but found None"

    shouldThrow<AssertionError> {
      Option.fromNullable("boo") shouldBeSome "foo"
    }.message shouldBe "expected:<\"foo\"> but was:<\"boo\">"

    val some =
      Option.fromNullable("foo") shouldBeSome "foo"

    shouldThrow<AssertionError> {
      some shouldNotBe "foo"
    }
  }

  "Option shouldBe none()" {
    shouldThrow<AssertionError> {
      Option.fromNullable("foo").shouldBeNone()
    }.message shouldBe "Expected None, but found Some with value foo"

    Option.fromNullable<String>(null).shouldBeNone()
  }
})
