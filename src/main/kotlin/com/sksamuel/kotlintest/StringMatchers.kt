package com.sksamuel.kotlintest

interface StringMatchers {

  infix fun Have<String>.substring(substr: String): Matcher<String> {
    return object : Matcher<String> {
      override fun apply(value: String): Unit {
        if (!value.contains(substr))
          throw TestFailedException("String does not have substring $substr")
      }
    }
  }

  infix fun Start<String>.with(prefix: String): Matcher<String> {
    return object : Matcher<String> {
      override fun apply(value: String): Unit {
        if (!value.startsWith(prefix))
          throw TestFailedException("String does not start with $prefix but with ${value.take(prefix.length)}")
      }
    }
  }

  infix fun End<String>.with(suffix: String): Matcher<String> {
    return object : Matcher<String> {
      override fun apply(value: String): Unit {
        if (!value.endsWith(suffix))
          throw TestFailedException("String does not end with $suffix but with ${value.takeLast(suffix.length)}")
      }
    }
  }
}