package io.kotest.assertions.arrow.fx.coroutines

import arrow.fx.coroutines.Resource
import arrow.fx.coroutines.resourceScope

@IgnorableReturnValue
public suspend infix fun <A> Resource<A>.shouldBeResource(a: A): A =
  resourceScope {
    bind() shouldBe a
  }

@IgnorableReturnValue
public suspend infix fun <A> Resource<A>.shouldBeResource(expected: Resource<A>): A =
  resourceScope {
    bind() shouldBe expected.bind()
  }
