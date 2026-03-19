package io.kotest.property.arrow.laws

import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.core.names.TestNameBuilder
import io.kotest.core.spec.style.TestXMethod
import io.kotest.core.spec.style.scopes.RootScope
import io.kotest.core.spec.style.scopes.addTest
import io.kotest.core.test.TestScope

public data class Law(val name: String, val test: suspend TestScope.() -> Unit)

public fun <A> A.equalUnderTheLaw(other: A, f: (A, A) -> Boolean = { a, b -> a == b }): Boolean =
   if (f(this, other)) true else AssertionErrorBuilder.fail("Found $this but expected: $other")

public fun RootScope.testLaws(vararg laws: List<Law>): Unit =
   laws
      .flatMap { list: List<Law> -> list.asIterable() }
      .distinctBy { law: Law -> law.name }
      .forEach { law ->
         addTest(
            TestNameBuilder.builder(law.name).build(),
            xmethod = TestXMethod.NONE,
            config = null
         ) { law.test(this) }
      }

public fun RootScope.testLaws(prefix: String, vararg laws: List<Law>): Unit =
   laws
      .flatMap { list: List<Law> -> list.asIterable() }
      .distinctBy { law: Law -> law.name }
      .forEach { law: Law ->
         addTest(
            TestNameBuilder.builder(law.name)
               .withPrefix(prefix)
               .withDefaultAffixes()
               .build(),
            xmethod = TestXMethod.NONE,
            config = null
         ) { law.test(this) }
      }
