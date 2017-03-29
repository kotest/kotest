package io.kotlintest.matchers

interface Matcher<T> {

  fun test(value: T): Result

  infix fun and(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val r = this@Matcher.test(value)
      if (!r.passed)
        return r
      else
        return other.test(value)
    }
  }

  infix fun or(other: Matcher<T>): Matcher<T> = object : Matcher<T> {
    override fun test(value: T): Result {
      val r = this@Matcher.test(value)
      if (r.passed)
        return r
      else
        return other.test(value)
    }
  }
}

inline fun <reified T> shouldThrow(thunk: () -> Any?): T {
  val e = try {
    thunk()
    null
  } catch (e: Throwable) {
    e
  }

  val exceptionClassName = T::class.qualifiedName

  if (e == null)
    throw AssertionError("Expected exception ${T::class.qualifiedName} but no exception was thrown")
  else if (e.javaClass.canonicalName != exceptionClassName)
    throw AssertionError("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown", e)
  else
    return e as T
}

data class Result(val passed: Boolean, val message: String)