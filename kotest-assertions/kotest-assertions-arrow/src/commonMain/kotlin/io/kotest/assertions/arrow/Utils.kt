package io.kotest.assertions.arrow

import io.kotest.matchers.shouldBe as coreShouldBe
import io.kotest.matchers.shouldNotBe as coreShouldNotBe

internal infix fun <A> A.shouldBe(a: A): A {
  this coreShouldBe a
  return this
}


internal infix fun <A> A.shouldNotBe(a: A?): A {
  this coreShouldNotBe a
  return this
}
