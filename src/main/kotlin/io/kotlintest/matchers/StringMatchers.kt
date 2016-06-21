package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface StringMatchers {

  infix fun HaveWrapper<String>.substring(substr: String): Unit {
    if (!value.contains(substr))
      throw TestFailedException("String does not have substring $substr")
  }

  infix fun StartWrapper<String>.with(prefix: String): Unit {
    if (!value.startsWith(prefix))
      throw TestFailedException("String does not start with $prefix but with ${value.take(prefix.length)}")
  }

  infix fun EndWrapper<String>.with(suffix: String): Unit {
    if (!value.endsWith(suffix))
      throw TestFailedException("String does not end with $suffix but with ${value.takeLast(suffix.length)}")
  }

  fun match(regex: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (!value.matches(regex.toRegex()))
        throw TestFailedException("String $value does not match regex $regex")
    }
  }

  fun haveLength(length: Int): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (value.length != length)
        throw TestFailedException("String $value does not have length $length")
    }
  }
}
