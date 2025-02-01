package io.kotest.property.arrow.laws

import io.kotest.assertions.fail
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestScope

public data class Law(val name: String, val test: suspend TestScope.() -> Unit)

public fun <A> A.equalUnderTheLaw(other: A, f: (A, A) -> Boolean = { a, b -> a == b }): Boolean =
  if (f(this, other)) true else fail("Found $this but expected: $other")

public fun RootScope.testLaws(vararg laws: List<Law>): Unit =
  laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law ->
      addTest(TestName(law.name), disabled = false, config = null) { law.test(this) }
    }

public fun RootScope.testLaws(prefix: String, vararg laws: List<Law>): Unit =
  laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .forEach { law: Law ->
      addTest(TestName(prefix, law.name, true), disabled = false, config = null) { law.test(this) }
    }
