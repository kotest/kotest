package io.kotlintest.matchers

interface Matcher<T> {

  fun test(value: T)

  infix fun and(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T) {
      // just test both as if one fails that exception will propagate through
      this@Matcher.test(value)
      other.test(value)
    }
  }

  infix fun or(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T) {
      // if first one fails, should give second one chance to run
      try {
        this@Matcher.test(value)
      } catch (e: AssertionError) {
        other.test(value)
      }
    }
  }
}