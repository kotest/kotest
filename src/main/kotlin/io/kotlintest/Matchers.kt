package io.kotlintest

interface Matchers : StringMatchers, LongMatchers, IntMatchers, CollectionMatchers, TypeMatchers {

  infix fun Any.shouldBe(any: Any): Unit = shouldEqual(any)
  infix fun Any.shouldEqual(any: Any): Unit {
    if (!this.equals(any))
      throw TestFailedException(this.toString() + " did not equal $any")
  }

  infix fun <T> T.should(matcher: (T) -> Unit): Unit = matcher(this)
  infix fun <T> T.shouldHave(matcher: (T) -> Unit): Unit = matcher(this)
  infix fun <T> T.shouldBe(matcher: (T) -> Unit): Unit = matcher(this)

  infix fun <T> T.should(x: have): Have<T> = Have(this)
  infix fun <T> T.should(x: start): Start<T> = Start(this)
  infix fun <T> T.should(x: end): End<T> = End(this)
  infix fun <T> T.should(x: be): Be<T> = Be(this)
  infix fun <T> T.should(x: contain): Contain<T> = Contain(this)
  infix fun <T> T.should(x: include): Include<T> = Include(this)

  interface Keyword

  object have : Keyword

  object be : Keyword

  object end : Keyword

  object start : Keyword

  object contain : Keyword

  object include : Keyword
}

class Have<T>(override val value: T) : Assertable<T>
class Be<T>(override val value: T) : Assertable<T>
class Start<T>(override val value: T) : Assertable<T>
class End<T>(override val value: T) : Assertable<T>
class Include<T>(override val value: T) : Assertable<T>
class Contain<T>(override val value: T) : Assertable<T>

interface Assertable<T> {
  val value: T
  infix fun T.should(matcher: (T) -> Unit): Unit = matcher(value)
  infix fun T.shouldHave(matcher: (T) -> Unit): Unit = matcher(value)
  infix fun T.shouldBe(matcher: (T) -> Unit): Unit = matcher(value)
}

