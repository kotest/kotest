package io.kotest.assertions.throwables

import io.kotest.assertions.collectOrThrow
import io.kotest.assertions.AssertionErrorBuilder
import io.kotest.assertions.print.StringPrint
import io.kotest.assertions.print.print
import io.kotest.matchers.assertionCounter
import io.kotest.matchers.errorCollector

/**
 * Verifies that a block of code throws any [Throwable]
 *
 * Use function to wrap a block of code that you want to verify that throws any kind of [Throwable], where using
 * [shouldThrowAny] can't be used for any reason, such as an assignment of a variable (assignments are statements,
 * therefore has no return value).
 *
 * If you want to verify a specific [Throwable], use [shouldThrowExactlyUnit].
 *
 * If you want to verify a [Throwable] and any subclass, use [shouldThrowUnit].
 *
 * @see [shouldThrowAny]
 */
inline fun shouldThrowAnyUnit(block: () -> Unit) = shouldThrowAny(block)

/**
 * Verifies that a block of code does NOT throw any [Throwable]
 *
 * Use function to wrap a block of code that you want to make sure doesn't throw any [Throwable],
 * where using [shouldNotThrowAny] can't be used for any reason, such as an assignment of a variable (assignments are
 * statements, therefore has no return value).
 *
 * Note that executing this code is no different to executing [block] itself, as any uncaught exception will
 * be propagated up anyways.
 *
 * @see [shouldNotThrowAny]
 * @see [shouldNotThrowExactlyUnit]
 * @see [shouldNotThrowUnit]
 *
 */
inline fun shouldNotThrowAnyUnit(block: () -> Unit) {
   assertionCounter.inc()

   val thrownException = try {
      block()
      null
   } catch (e: Throwable) {
      e
   }

   thrownException?.let {
      errorCollector.collectOrThrow(
         AssertionErrorBuilder.create()
            .withMessage("No exception expected, but a ${thrownException::class.simpleName} was thrown with message: \"${thrownException.message}\".")
            .withCause(thrownException)
            .build()
      )
   }
}

/**
 * Verifies that a block of code throws any [Throwable]
 *
 * Use function to wrap a block of code that you want to verify that throws any kind of [Throwable].
 *
 * If you want to verify a specific [Throwable], use [shouldThrowExactly].
 *
 * If you want to verify a [Throwable] and any subclasses, use [shouldThrow]
 *
 *
 * **Attention to assignment operations:**
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment throws a [Throwable], use [shouldThrowAnyUnit] or it's variations.
 *
 * ```
 *     val thrownThrowable: Throwable = shouldThrowAny {
 *         throw FooException() // This is a random Throwable, could be anything
 *     }
 * ```
 *
 * @see [shouldThrowAnyUnit]
 */
inline fun shouldThrowAny(block: () -> Any?): Throwable {
   assertionCounter.inc()
   val thrownException = try {
      block()
      null
   } catch (e: Throwable) {
      e
   }

   return thrownException ?: AssertionErrorBuilder.fail("Expected a throwable, but nothing was thrown.")
}


/**
 * Verifies that a block of code does NOT throw any [Throwable]
 *
 * Use function to wrap a block of code that you want to make sure doesn't throw any [Throwable].
 *
 * Note that executing this code is no different to executing [block] itself, as any uncaught exception will
 * be propagated up anyways.
 *
 * **Attention to assignment operations:**
 *
 * When doing an assignment to a variable, the code won't compile, because an assignment is not of type [Any], as required
 * by [block]. If you need to test that an assignment doesn't throw a [Throwable], use [shouldNotThrowAnyUnit] or it's
 * variations.
 *
 * @see [shouldNotThrowAnyUnit]
 * @see [shouldNotThrowExactly]
 * @see [shouldNotThrow]
 *
 */
inline fun <T> shouldNotThrowAny(block: () -> T): T {
   assertionCounter.inc()

   val thrownException = try {
      return block()
   } catch (e: Throwable) {
      e
   }

   throw AssertionErrorBuilder.create()
      .withMessage("No exception expected, but a ${thrownException::class.simpleName} was thrown with message: \"${thrownException.message}\".")
      .withCause(thrownException)
      .build()
}


/**
 * Verifies that a block of code throws any [Throwable] with given [message].
 *
 * @see [shouldNotThrowMessage]
 * */
inline fun <T> shouldThrowMessage(message: String, block: () -> T) {
   assertionCounter.inc()

   val thrownException = try {
      block()
      null
   } catch (e: Throwable) {
      e
   }

   thrownException ?: throw AssertionErrorBuilder.create()
      .withMessage("Expected a throwable with message ${StringPrint.print(message, 0).value} but nothing was thrown".trimMargin())
      .build()

   if (thrownException.message != message) {
      throw AssertionErrorBuilder.create()
         .withMessage("Expected a throwable with message ${StringPrint.print(message, 0).value} but got a throwable with message ${thrownException.message.print().value}".trimMargin())
         .withCause(thrownException)
         .build()
   }
}

/**
 * Verifies that a block of code does not throws any [Throwable] with given [message].
* */
inline fun <T> shouldNotThrowMessage(message: String, block: () -> T) {
   assertionCounter.inc()

   val thrownException = try {
      block()
      null
   } catch (e: Throwable) {
      e
   }

   if (thrownException != null && thrownException.message == message) {
      throw AssertionErrorBuilder.create()
         .withMessage(
            """Expected no exception with message: "$message"
                          |but a ${thrownException::class.simpleName} was thrown with given message""".trimMargin()
         )
         .withCause(thrownException)
         .build()
   }
}
