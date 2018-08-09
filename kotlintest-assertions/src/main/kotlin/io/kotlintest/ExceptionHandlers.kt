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
inline fun <reified T : Throwable> shouldThrow(thunk: () -> Any?): T {
  val exceptionClass = T::class.java
  try {
    thunk()
    throw Failures.failure("Expected exception ${T::class.qualifiedName} but no exception was thrown")
  } catch (e: Throwable) {
    return when {
      exceptionClass.isAssignableFrom(e.javaClass) -> e as T
      e is AssertionError -> throw e
      else -> throw Failures.failure("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown", e)
    }
  }
}

inline fun <reified T : Throwable> shouldThrowUnit(thunk: () -> Unit): T {
  val exceptionClass = T::class.java
  try {
    thunk()
    throw Failures.failure("Expected exception ${T::class.qualifiedName} but no exception was thrown")
  } catch (e: Throwable) {
    return when {
      exceptionClass.isAssignableFrom(e.javaClass) -> e as T
      e is AssertionError -> throw e
      else -> throw Failures.failure("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown", e)
    }
  }
}

fun shouldFail(thunk: () -> Any?) {
  val passed = try {
    thunk()
    true
  } catch (e: AssertionError) {
    false
  }
  if (passed)
    throw Failures.failure("This block should fail by throwing by exception but not exception was thrown")
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
inline fun <reified T : Throwable> shouldThrowExactly(thunk: () -> Any?): T {
  val exceptionClass = T::class.java
  try {
    thunk()
    throw Failures.failure("Expected exception ${T::class.qualifiedName} but no exception was thrown")
  } catch (e: Throwable) {
    return when {
      e.javaClass == exceptionClass -> e as T
      e is AssertionError -> throw e
      else -> throw Failures.failure("Expected exception ${T::class.qualifiedName} but ${e.javaClass.name} was thrown", e)
    }
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
    throw Failures.failure("Expected exception but no exception was thrown")
  else
    return e
}
