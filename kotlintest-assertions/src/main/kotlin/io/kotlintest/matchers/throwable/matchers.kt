package io.kotlintest.matchers.throwable

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

inline fun <reified T : Throwable> Throwable.shouldBeOfType() = this should beOfType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotBeOfType() = this shouldNot beOfType<T>()
inline fun <reified T : Throwable> beOfType() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = Result(
      value is T,
      "Value should be of type ${T::class}, but instead got ${value::class}",
      "Value should not be of type ${T::class}"
  )
}

inline fun <reified T : Throwable> Throwable.shouldBeExactlyOfType() = this should beOfTypeExactly<T>()
inline fun <reified T : Throwable> Throwable.shouldNotBeExactlyOfType() = this shouldNot beOfTypeExactly<T>()
inline fun <reified T : Throwable> beOfTypeExactly() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = Result(
      value::class == T::class,
      "Value should be of type ${T::class}, but instead got ${value::class}",
      "Value should not be of type ${T::class}"
  )
}

infix fun Throwable.shouldHaveMessage(message: String) = this should haveMessage(message)
infix fun Throwable.shouldNotHaveMessage(message: String) = this shouldNot haveMessage(message)
fun haveMessage(message: String) = object : Matcher<Throwable> {
  override fun test(value: Throwable) = Result(
      value.message == message,
      "Throwable should have message \"$message\", but instead got ${value.message}",
      "Throwable should not have message \"$message\""
  )
}

fun Throwable.shouldHaveCause(block: ((Throwable) -> Unit)? = null) {
  this should haveCause()
  block?.invoke(cause!!)
}
fun Throwable.shouldNotHaveCause() = this shouldNot haveCause()
fun haveCause() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = Result(
      value.cause != null,
      "Throwable should have a cause",
      "Throwable should not have a cause"
  )
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseOfType() = this should haveCauseOfType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseOfType() = this shouldNot haveCauseOfType<T>()
inline fun <reified T : Throwable> haveCauseOfType() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = when {
    value.cause == null -> Result(
        false,
        "Throwable should have a cause",
        "Throwable should not have a cause"
    )
    else -> Result(
        value.cause is T,
        "Throwable cause should be of type ${T::class}, but instead got ${value::class}",
        "Throwable cause should be of type ${T::class}"
    )
  }
}

inline fun <reified T : Throwable> Throwable.shouldHaveCauseOfExacltyType() = this should haveCauseOfTExacltyType<T>()
inline fun <reified T : Throwable> Throwable.shouldNotHaveCauseOfExacltyType() = this shouldNot haveCauseOfTExacltyType<T>()
inline fun <reified T : Throwable> haveCauseOfTExacltyType() = object : Matcher<Throwable> {
  override fun test(value: Throwable) = when {
    value.cause == null -> Result(
        false,
        "Throwable should have a cause",
        "Throwable should not have a cause"
    )
    else -> Result(
        value.cause!!::class == T::class,
        "Throwable cause should be of type ${T::class}, but instead got ${value::class}",
        "Throwable cause should be of type ${T::class}"
    )
  }
}