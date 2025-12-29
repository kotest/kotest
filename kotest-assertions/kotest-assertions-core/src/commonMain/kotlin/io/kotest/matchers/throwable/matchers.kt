package io.kotest.matchers.throwable

import io.kotest.assertions.print.print
import io.kotest.common.reflection.bestName
import io.kotest.matchers.ComparisonMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

infix fun Throwable.shouldHaveMessage(message: String): Throwable {
   this should haveMessage(message)
   return this
}

infix fun Throwable.shouldNotHaveMessage(message: String): Throwable {
   this shouldNot haveMessage(message)
   return this
}

fun haveMessage(message: String) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = ComparisonMatcherResult(
      value.message?.trim() == message.trim(),
      actual = value.message?.trim().print(),
      expected = message.trim().print(),
      failureMessageFn = {
         "Throwable should have message:\n${message.trim().print().value}\n\nActual was:\n${
            value.message?.trim().print().value
         }\n"
      },
      negatedFailureMessageFn = {
         "Throwable should not have message:\n${message.trim().print().value}"
      },
   )
}

infix fun Throwable.shouldHaveMessage(message: Regex): Throwable {
   this should haveMessage(message)
   return this
}

infix fun Throwable.shouldNotHaveMessage(message: Regex): Throwable {
   this shouldNot haveMessage(message)
   return this
}

fun haveMessage(regex: Regex) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = MatcherResult(
      value.message?.matches(regex) ?: false,
      { "Throwable should match regex: ${regex.print().value}\nActual was:\n${value.message?.trim().print().value}\n" },
      { "Throwable should not match regex: ${regex.print().value}" })
}


fun Throwable.shouldHaveCause(block: (Throwable) -> Unit = {}): Throwable {
  this should haveCause()
  block.invoke(cause!!)
  return this
}

fun Throwable.shouldNotHaveCause(): Throwable {
  this shouldNot haveCause()
  return this
}
fun haveCause() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = resultForThrowable(value.cause)
}

infix fun Throwable.shouldHaveStackTraceContaining(substr: String): Throwable {
  this should haveStackTraceContaining(substr)
  return this
}
infix fun Throwable.shouldNotHaveStackTraceContaining(substr: String): Throwable {
  this shouldNot haveStackTraceContaining(substr)
  return this
}
fun haveStackTraceContaining(substr: String) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = MatcherResult(
      value.stackTraceToString().contains(substr),
      { "Throwable stacktrace should contain substring: ${substr.print().value}\nActual was:\n${value.stackTraceToString().print().value}" },
      { "Throwable stacktrace should not contain substring: ${substr.print().value}" })
}

infix fun Throwable.shouldHaveStackTraceContaining(regex: Regex): Throwable {
  this should haveStackTraceContaining(regex)
  return this
}
infix fun Throwable.shouldNotHaveStackTraceContaining(regex: Regex): Throwable {
  this shouldNot haveStackTraceContaining(regex)
  return this
}
fun haveStackTraceContaining(regex: Regex) = object : Matcher<Throwable> {
   override fun test(value: Throwable) = MatcherResult(
      value.stackTraceToString().contains(regex),
      { "Throwable stacktrace should contain regex: ${regex.print().value}\nActual was:\n${value.stackTraceToString().print().value}" },
      { "Throwable stacktrace should not contain regex: ${regex.print().value}" })
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseInstanceOf(): Throwable {
  this should haveCauseInstanceOf<T>()
  return this
}
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseInstanceOf(): Throwable {
  this shouldNot haveCauseInstanceOf<T>()
  return this
}
inline fun <reified T : Throwable> haveCauseInstanceOf() = object : Matcher<Throwable> {
   override fun test(value: Throwable) = when (val cause = value.cause) {
      null -> resultForThrowable(null)
      else -> MatcherResult(
         cause is T,
         { "Throwable cause should be of type ${T::class.bestName()} or it's descendant, but instead got ${cause::class.bestName()}" },
         { "Throwable cause should not be of type ${T::class.bestName()} or it's descendant" })
   }
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseOfType(): Throwable {
  this should haveCauseOfType<T>()
  return this
}
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseOfType(): Throwable {
  this shouldNot haveCauseOfType<T>()
  return this
}
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
