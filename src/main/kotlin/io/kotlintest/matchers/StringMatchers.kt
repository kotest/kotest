package io.kotlintest.matchers

object have : ShouldKeyword<have>
object start : ShouldKeyword<start>
object end : ShouldKeyword<end>

@JvmName("startWith") infix fun ShouldBuilder<start, String>.with(prefix: String): Unit {
  if (!value.startsWith(prefix))
    throw AssertionError("String does not start with $prefix but with ${value.take(prefix.length)}")
}

@JvmName("endWith") infix fun ShouldBuilder<end, String>.with(suffix: String): Unit {
  if (!value.endsWith(suffix))
    throw AssertionError("String does not end with $suffix but with ${value.takeLast(suffix.length)}")
}

infix fun ShouldBuilder<have, String>.substring(substr: String): Unit {
  if (!value.contains(substr))
    throw AssertionError("String does not have substring $substr")
}


interface StringMatchers {

  fun startWith(prefix: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (!value.startsWith(prefix))
        throw AssertionError("String $value does not start with $prefix")
    }
  }

  fun include(substr: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (!value.contains(substr))
        throw AssertionError("String $value does not include substring $substr")
    }
  }

  fun endWith(suffix: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (!value.endsWith(suffix))
        throw AssertionError("String $value does not end with with $suffix")
    }
  }

  fun match(regex: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (!value.matches(regex.toRegex()))
        throw AssertionError("String $value does not match regex $regex")
    }
  }

  fun haveLength(length: Int): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) {
      if (value.length != length)
        throw AssertionError("String $value does not have length $length")
    }
  }
}
