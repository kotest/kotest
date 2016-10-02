package io.kotlintest.matchers

import io.kotlintest.Inspectors

interface ShouldKeyword<K> {
  fun <T> wrapper(value: T): ShouldBuilder<K, T> = ShouldBuilder<K, T>(value)
}

class ShouldBuilder<K, T>(val value: T)

interface Matchers : StringMatchers,
    CollectionMatchers,
    DoubleMatchers,
    IntMatchers,
    LongMatchers,
    MapMatchers,
    TypeMatchers,
    Inspectors {

  fun fail(msg: String): Nothing = throw AssertionError(msg)

  infix fun Double.shouldBe(other: Double): Unit = ToleranceMatcher(other, 0.0).test(this)
  infix fun <T> T.shouldBe(any: Any?): Unit = shouldEqual(any)
  infix fun <T> T.shouldEqual(any: Any?): Unit {
    when (any) {
      is Matcher<*> -> (any as Matcher<T>).test(this)
      else -> {
        if (this == null && any != null)
          throw AssertionError(this.toString() + " did not equal $any")
        if (this != null && any == null)
          throw AssertionError(this.toString() + " did not equal $any")
        if (this != any)
          throw AssertionError(this.toString() + " did not equal $any")
      }
    }
  }

  infix fun <T> T.should(matcher: (T) -> Unit): Unit = matcher(this)
  infix fun <T> T.should(matcher: Matcher<T>) = matcher.test(this)
  infix fun <K, T> T.should(keyword: ShouldKeyword<K>): ShouldBuilder<K, T> = keyword.wrapper(this)
}