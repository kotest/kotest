package io.kotlintest.matchers

import io.kotlintest.Inspectors

interface Keyword

object have : Keyword

object be : Keyword

object end : Keyword

object start : Keyword

object contain : Keyword

object include : Keyword

interface Matchers : StringMatchers,
    CollectionMatchers,
    ComparableMatchers,
    DoubleMatchers,
    ExceptionMatchers,
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
      is io.kotlintest.matchers.Matcher<*> -> (any as Matcher<T>).test(this)
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
  infix fun <T> T.should(x: have): HaveWrapper<T> = HaveWrapper(this)
  infix fun <T> T.should(x: start): StartWrapper<T> = StartWrapper(this)
  infix fun <T> T.should(x: end): EndWrapper<T> = EndWrapper(this)
  infix fun <T> T.should(x: be): BeWrapper<T> = BeWrapper(this)
  infix fun <T> T.should(x: contain): ContainWrapper<T> = ContainWrapper(this)
  infix fun <T> T.should(x: include): IncludeWrapper<T> = IncludeWrapper(this)
}

interface Matcher<T> {
  fun test(value: T)
}

class HaveWrapper<T>(val value: T)
class BeWrapper<T>(val value: T)
class StartWrapper<T>(val value: T)
class EndWrapper<T>(val value: T)
class IncludeWrapper<T>(val value: T)
class ContainWrapper<T>(val value: T)