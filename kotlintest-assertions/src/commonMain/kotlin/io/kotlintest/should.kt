package io.kotlintest

expect infix fun <T> T.should(matcher: Matcher<T>)
expect infix fun <T> T.shouldNot(matcher: Matcher<T>)