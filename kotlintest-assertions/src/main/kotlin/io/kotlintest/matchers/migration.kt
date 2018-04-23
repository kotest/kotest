package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.shouldNot
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldThrow

infix fun <T, U : T> T.shouldBe(any: U?) = shouldBe(any)
infix fun <T> T.shouldNotBe(any: Any?) = shouldNotBe(any)
infix fun <T> T.shouldNot(matcher: Matcher<T>) = shouldNot(matcher)
infix fun <T> T.shouldHave(matcher: Matcher<T>) = shouldHave(matcher)
infix fun <T> T.should(matcher: Matcher<T>) = should(matcher)
inline fun <reified T : Throwable> shouldThrow(thunk: () -> Any?): T = shouldThrow(thunk)
