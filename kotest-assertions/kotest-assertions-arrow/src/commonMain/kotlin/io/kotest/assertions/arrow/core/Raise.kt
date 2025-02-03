package io.kotest.assertions.arrow.core

import arrow.core.raise.Raise
import arrow.core.raise.recover
import io.kotest.assertions.failure
import io.kotest.assertions.print.print

/**
 * Verifies if a block of code will raise a specified type of [T] (or subclasses).
 *
 * This function will include subclasses of [T]. For example, if you test for [CharSequence] and the code raises
 * [String], the test will pass.
 *
 * ```kotlin
 * val raised: String = shouldRaise<String> {
 *   raise("failed")
 * }
 * ```
 */
public inline fun <reified T> shouldRaise(block: Raise<Any?>.() -> Any?): T {
  val expectedRaiseClass = T::class
  return recover({
    block(this)
    throw failure("Expected to raise ${expectedRaiseClass.simpleName} but nothing was raised.")
  }) { raised ->
    when (raised) {
      is T -> raised
      null -> throw failure("Expected to raise ${expectedRaiseClass.simpleName} but <null> was raised instead.")
      else -> throw failure("Expected to raise ${expectedRaiseClass.simpleName} but ${raised::class.simpleName} was raised instead.")
    }
  }
}

/**
 * Verifies that a block of code will not raise anything.
 *
 * ```kotlin
 * val raised: String = shouldNotRaise {
 *   raise("failed") // fails
 * }
 * ```
 */
public inline fun <T> shouldNotRaise(block: Raise<Any?>.() -> T): T {
  return recover(block) { raised ->
    throw failure("No raise expected, but ${raised.print().value} was raised.")
  }
}
