package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.shouldHave
import io.kotlintest.shouldNotBe
import io.kotlintest.shouldNot
import io.kotlintest.shouldThrow

@Deprecated("use the equivalent function io.kotlintest.shouldBe", ReplaceWith("shouldBe(matcher)", "io.kotlintest.shouldBe"))
infix fun <T> T.shouldBe(matcher: Matcher<T>) = shouldBe(matcher)

@Deprecated("use the equivalent function io.kotlintest.shouldNotBe", ReplaceWith("shouldNotBe(matcher)", "io.kotlintest.shouldNotBe"))
infix fun <T> T.shouldNotBe(matcher: Matcher<T>) = shouldNotBe(matcher)

@Deprecated("use the equivalent function io.kotlintest.shouldNot", ReplaceWith("shouldNot(matcher)", "io.kotlintest.shouldNot"))
infix fun <T> T.shouldNot(matcher: Matcher<T>) = shouldNot(matcher)

@Deprecated("use the equivalent function io.kotlintest.shouldHave", ReplaceWith("shouldHave(matcher)", "io.kotlintest.shouldHave"))
infix fun <T> T.shouldHave(matcher: Matcher<T>) = shouldHave(matcher)

@Deprecated("use the equivalent function io.kotlintest.should", ReplaceWith("should(matcher)", "io.kotlintest.should"))
infix fun <T> T.should(matcher: Matcher<T>) = should(matcher)

@Deprecated("use the equivalent function io.kotlintest.shouldThrow", ReplaceWith("shouldThrow(matcher)", "io.kotlintest.shouldThrow"))
inline fun <reified T : Throwable> shouldThrow(thunk: () -> Any?): T = shouldThrow(thunk)
