package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resourceScope

public suspend infix fun <A> Resource<A>.shouldBeResource(a: A): A =
  resourceScope {
    bind() shouldBe a
  }

public suspend infix fun <A> Resource<A>.shouldBeResource(expected: Resource<A>): A =
  resourceScope {
    bind() shouldBe expected.bind()
  }
