package io.kotlintest

/**
 * Use this function to wrap a block of code where you wish
 * to assert that the code block throws an exception <T>.
 *
 * This function will include subclasses of <T>. For example, if you
 * test for [java.io.IOException] and the code block throws
 * [java.io.FileNotFoundException] then the test will pass.
 *
 * If you wish to test for a specific class only, excluding subclasses
 * then use `shouldThrowExactly<T>`
 */
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

/**
 * Use this function to wrap a block of code where you wish
 * to assert that the code block throws a specific exception <T>.
 *
 * This function will exclude subclasses of <T>. For example, if you
 * test for [java.io.IOException] and the code block throws
 * [java.io.FileNotFoundException] then the test will fail.
 *
 * If you wish to include subclasses of exceptions, then
 * use `shouldThrow<T>`
 */
inline fun <reified T> shouldThrowExactly(thunk: () -> Any?): T {
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

/**
 * Use this function to wrap a block of code where you wish
 * to assert that the code block throws any subclass of Throwable.
 */
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