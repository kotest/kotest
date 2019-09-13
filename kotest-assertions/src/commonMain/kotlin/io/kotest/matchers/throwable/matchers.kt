package io.kotest.matchers.throwable

import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.assertions.stringRepr
import io.kotest.should
import io.kotest.shouldNot

infix fun Throwable.shouldHaveMessage(message: String) = this should haveMessage(message)
infix fun Throwable.shouldNotHaveMessage(message: String) = this shouldNot haveMessage(message)

fun haveMessage(message: String) = object : Matcher<Throwable> {
  override fun test(value: Throwable) = MatcherResult(
    value.message == message,
    "Throwable should have message ${stringRepr(message)}, but instead got ${stringRepr(value.message)}",
    "Throwable should not have message ${stringRepr(message)}"
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
  override fun test(value: Throwable) = when {
    value.cause == null -> resultForThrowable(value.cause)
    else -> MatcherResult(
        value.cause is T,
        "Throwable cause should be of type ${T::class}, but instead got ${value::class}",
        "Throwable cause should be of type ${T::class}"
    )
  }
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseOfType() = this should haveCauseOfType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseOfType() = this shouldNot haveCauseOfType<T>()
inline fun <reified T : Throwable> haveCauseOfType() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = when {
    value.cause == null -> resultForThrowable(value.cause)
    else -> MatcherResult(
        value.cause!!::class == T::class,
        "Throwable cause should be of type ${T::class}, but instead got ${value::class}",
        "Throwable cause should be of type ${T::class}"
    )
  }
}

@PublishedApi
internal fun resultForThrowable(value: Throwable?) = MatcherResult(
    value != null,
    "Throwable should have a cause",
    "Throwable should not have a cause"
)
