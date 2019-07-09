package io.kotlintest.matchers.reflection

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import kotlin.reflect.KCallable
import kotlin.reflect.KVisibility

infix fun KCallable<*>.shouldHaveVisibility(visibility: KVisibility) = this should haveCallableVisibility(visibility)
infix fun KCallable<*>.shouldNotHaveVisibility(visibility: KVisibility) = this shouldNot haveCallableVisibility(visibility)
fun haveCallableVisibility(expected: KVisibility) = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.visibility == expected,
      "Member $value should have visibility ${expected.humanName()}",
      "Member $value should not have visibility ${expected.humanName()}"
  )
}

fun KCallable<*>.shouldBeFinal() = this should beFinal()
fun KCallable<*>.shouldNotBeFinal() = this shouldNot beFinal()
fun beFinal() = object : Matcher<KCallable<*>> {
  override fun test(value: KCallable<*>) = Result(
      value.isFinal,
      "Member $value should be final",
      "Member $value should not be final"
  )
}