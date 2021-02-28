package io.kotest.matchers.throwable

import io.kotest.assertions.show.show
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Throwable.shouldHaveMessage(message: String) = this should haveMessage(message)
infix fun Throwable.shouldNotHaveMessage(message: String) = this shouldNot haveMessage(message)

fun haveMessage(message: String) = object : Matcher<Throwable> {
  override fun test(value: Throwable) = MatcherResult(
    value.message?.trim() == message.trim(),
    "Throwable should have message:\n${message.trim().show().value}\n\nActual was:\n${value.message?.trim().show().value}\n",
    "Throwable should not have message:\n${message.trim().show().value}"
  )
}

fun Throwable.shouldHaveCause(block: (Throwable) -> Unit = {}) {
  this should haveCause()
  block.invoke(cause!!)
}

fun Throwable.shouldNotHaveCause() = this shouldNot haveCause()
fun haveCause() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = resultForThrowable(value.cause)
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseInstanceOf() = this should haveCauseInstanceOf<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseInstanceOf() = this shouldNot haveCauseInstanceOf<T>()
inline fun <reified T : Throwable> haveCauseInstanceOf() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = when (val cause = value.cause) {
    null -> resultForThrowable(null)
    else -> MatcherResult(
      cause is T,
      "Throwable cause should be of type ${T::class}, but instead got ${cause::class}",
      "Throwable cause should not be of type ${T::class}"
    )
  }
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseOfType() = this should haveCauseOfType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseOfType() = this shouldNot haveCauseOfType<T>()
inline fun <reified T : Throwable> haveCauseOfType() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = when (val cause = value.cause) {
    null -> resultForThrowable(null)
    else -> MatcherResult(
      cause::class == T::class,
      "Throwable cause should be of type ${T::class}, but instead got ${cause::class}",
      "Throwable cause should not be of type ${T::class}"
    )
  }
}

@PublishedApi
internal fun resultForThrowable(value: Throwable?) = MatcherResult(
  value != null,
  "Throwable should have a cause",
  "Throwable should not have a cause"
)
