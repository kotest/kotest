package io.kotest.matchers.throwable

import io.kotest.assertions.print.print
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.mpp.bestName

infix fun Throwable.shouldHaveMessage(message: String) = this should haveMessage(message)
infix fun Throwable.shouldNotHaveMessage(message: String) = this shouldNot haveMessage(message)

fun haveMessage(message: String) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = ComparableMatcherResult(
      value.message?.trim() == message.trim(),
      {
         "Throwable should have message:\n${message.trim().print().value}\n\nActual was:\n${
            value.message?.trim().print().value
         }\n"
      },
      {
         "Throwable should not have message:\n${message.trim().print().value}"
      },
      actual = value.message?.trim().print().value,
      expected = message.trim().print().value,
   )
}

infix fun Throwable.shouldHaveMessage(message: Regex) = this should haveMessage(message)
infix fun Throwable.shouldNotHaveMessage(message: Regex) = this shouldNot haveMessage(message)

fun haveMessage(regex: Regex) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = MatcherResult(
      value.message?.matches(regex) ?: false,
      { "Throwable should match regex: ${regex.print().value}\nActual was:\n${value.message?.trim().print().value}\n" },
      { "Throwable should not match regex: ${regex.print().value}" })
}


fun Throwable.shouldHaveCause(block: (Throwable) -> Unit = {}) {
  this should haveCause()
  block.invoke(cause!!)
}

fun Throwable.shouldNotHaveCause() = this shouldNot haveCause()
fun haveCause() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = resultForThrowable(value.cause)
}

infix fun Throwable.shouldHaveStacktraceContaining(substr: String) = this should haveStacktraceContaining(substr)
infix fun Throwable.shouldNotHaveStacktraceContaining(substr: String) = this shouldNot haveStacktraceContaining(substr)
fun haveStacktraceContaining(substr: String) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = MatcherResult(
      value.stackTraceToString().contains(substr),
      { "Throwable stacktrace should contain substring: ${substr.print().value}\nActual was:\n${value.stackTraceToString().print().value}" },
      { "Throwable stacktrace should not contain substring: ${substr.print().value}" })
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseInstanceOf() = this should haveCauseInstanceOf<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseInstanceOf() = this shouldNot haveCauseInstanceOf<T>()
inline fun <reified T : Throwable> haveCauseInstanceOf() = object : Matcher<Throwable> {
   override fun test(value: Throwable) = when (val cause = value.cause) {
      null -> resultForThrowable(null)
      else -> MatcherResult(
         cause is T,
         { "Throwable cause should be of type ${T::class.bestName()} or it's descendant, but instead got ${cause::class.bestName()}" },
         { "Throwable cause should not be of type ${T::class.bestName()} or it's descendant" })
   }
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseOfType() = this should haveCauseOfType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseOfType() = this shouldNot haveCauseOfType<T>()
inline fun <reified T : Throwable> haveCauseOfType() = object : Matcher<Throwable> {
   override fun test(value: Throwable) = when (val cause = value.cause) {
      null -> resultForThrowable(null)
      else -> MatcherResult(
         cause::class == T::class,
         { "Throwable cause should be of type ${T::class.bestName()}, but instead got ${cause::class.bestName()}" },
         { "Throwable cause should not be of type ${T::class.bestName()}" })
   }
}

@PublishedApi
internal fun resultForThrowable(value: Throwable?) = MatcherResult(
   value != null,
   { "Throwable should have a cause" },
   { "Throwable should not have a cause" })
