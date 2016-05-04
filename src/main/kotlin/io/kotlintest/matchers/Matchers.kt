package io.kotlintest.matchers

import io.kotlintest.Inspectors
import io.kotlintest.TestFailedException

interface Keyword

object have : Keyword

object be : Keyword

object end : Keyword

object start : Keyword

object contain : Keyword

object include : Keyword

interface Matchers : StringMatchers, LongMatchers, IntMatchers, CollectionMatchers, TypeMatchers, ExceptionMatchers, Inspectors {

  fun fail(msg: String) = throw TestFailedException(msg)

  infix fun <T> T.shouldBe(any: Any?): Unit = shouldEqual(any)
  infix fun <T> T.shouldEqual(any: Any?): Unit {
    if (this == null && any != null)
      throw TestFailedException(this.toString() + " did not equal $any")
    if (this != null && any == null)
      throw TestFailedException(this.toString() + " did not equal $any")
    if (this != any)
      throw TestFailedException(this.toString() + " did not equal $any")
  }

  infix fun <T> T.should(matcher: (T) -> Unit): Unit = matcher(this)
  infix fun <T> T.should(matcher: Matcher<T>) = matcher.test(this)
  infix fun <T> T.should(x: have): Have<T> = Have(this)
  infix fun <T> T.should(x: start): Start<T> = Start(this)
  infix fun <T> T.should(x: end): End<T> = End(this)
  infix fun <T> T.should(x: be): Be<T> = Be(this)
  infix fun <T> T.should(x: contain): Contain<T> = Contain(this)
  infix fun <T> T.should(x: include): Include<T> = Include(this)
}

interface Matcher<T> {
  fun test(value: T)
}

class Have<T>(val value: T)
class Be<T>(val value: T)
class Start<T>(val value: T)
class End<T>(val value: T)
class Include<T>(val value: T)
class Contain<T>(val value: T)