package io.kotest.property.arrow.laws

import io.kotest.matchers.shouldBe as coreShouldBe

@IgnorableReturnValue
internal infix fun <A> A.shouldBe(a: A): A {
  this coreShouldBe a
  return this
}
