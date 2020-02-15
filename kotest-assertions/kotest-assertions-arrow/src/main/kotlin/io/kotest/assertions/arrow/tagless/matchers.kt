package io.kotest.assertions.arrow.tagless

import arrow.Kind
import arrow.extension
import arrow.fx.ForIO
import arrow.fx.IO
import arrow.fx.extensions.io.applicative.applicative
import arrow.fx.fix
import arrow.typeclasses.Applicative
import io.kotest.assertions.arrow.matcher
import io.kotest.matchers.Matcher
import io.kotest.matchers.should

/**
 * Assertions for tagless final and polymorphic programs
 */
interface TaglessAssertions<F> {

  fun AF(): Applicative<F>

  fun <A> Kind<F, A>.be(a: A): Kind<F, Matcher<A>> =
    AF().run {
      map { x -> matcher<A>(x == a, "$x is not equal to $a") }
    }

  /**
   * Asserts that the given tagless program [this] can be reduced to [a]
   * blocking if necessary in async and effect capable data types such as
   * [IO]
   *
   * ```kotlin
   * fun <F> Applicative<F>.helloWorldPoly(): Kind<F, String> = just("Hello World")
   *
   * IO.applicative().run {
   *   helloWorldPoly() shouldBeInterpretedTo "Hello World"
   * }
   * ```
   */
  infix fun <A> Kind<F, A>.shouldBeInterpretedTo(a: A): Unit =
    AF().run {
      map(this@shouldBeInterpretedTo, be(a)) { (a, matcher) ->
        a should matcher
      }.blockingValue()
    }

  /**
   * Potentially blocks a running computation until it completes so that it's
   * reduced value can be asserted as the result of the program
   */
  fun <A> Kind<F, A>.blockingValue(): A

}

@extension
interface IOAssertions : TaglessAssertions<ForIO> {
  override fun AF(): Applicative<ForIO> = IO.applicative()

  override fun <A> Kind<ForIO, A>.blockingValue(): A = fix().unsafeRunSync()
}
