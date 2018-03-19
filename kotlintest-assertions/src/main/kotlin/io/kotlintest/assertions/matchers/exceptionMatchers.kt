package io.kotlintest.assertions.matchers

inline fun <reified T> shouldThrow(thunk: () -> Any?): T {
  val e = try {
    thunk()
    null
  } catch (e: Throwable) {
    e
  }

  val exceptionClassName = T::class.qualifiedName

  when {
    e == null -> throw AssertionError("Expected exception ${T::class.qualifiedName} but no exception was thrown")
    e.javaClass.canonicalName != exceptionClassName -> throw AssertionError("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown", e)
    else -> return e as T
  }
}

inline fun shouldThrowAny(thunk: () -> Any?): Throwable {
  val e = try {
    thunk()
    null
  } catch (e: Throwable) {
    e
  }

  if (e == null)
    throw AssertionError("Expected exception but no exception was thrown")
  else
    return e
}