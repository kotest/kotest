package io.kotlintest.matchers

object have : Keyword<have>

object start : Keyword<start>

object end : Keyword<end>

@Deprecated("Use `str should startWith(\"xyz\")`")
@JvmName("startWith") infix fun MatcherBuilder<start, String>.with(prefix: String): Unit {
  if (!value.startsWith(prefix))
    throw AssertionError("String does not start with $prefix but with ${value.take(prefix.length)}")
}

@Deprecated("Use `str should endWith(\"xyz\")`")
@JvmName("endWith") infix fun MatcherBuilder<end, String>.with(suffix: String): Unit {
  if (!value.endsWith(suffix))
    throw AssertionError("String does not end with $suffix but with ${value.takeLast(suffix.length)}")
}

@Deprecated("Use `str should include(\"xyz\")`")
infix fun MatcherBuilder<have, String>.substring(substr: String): Unit {
  if (!value.contains(substr))
    throw AssertionError("String does not have substring $substr")
}


interface StringMatchers {

  fun startWith(prefix: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String): Result {
      val ok = value.startsWith(prefix)
      var msg = "String $value should start with $prefix"
      if (!ok) {
        for (k in 0..Math.min(value.length, prefix.length) - 1) {
          if (value[k] != prefix[k]) {
            msg = "$msg (diverged at index $k)"
            break
          }
        }
      }
      return Result(ok, msg)
    }
  }

  fun include(substr: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) = Result(value.contains(substr), "String $value should include substring $substr")
  }

  fun endWith(suffix: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) = Result(value.endsWith(suffix), "String $value should end with $suffix")
  }

  fun match(regex: String): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) = Result(value.matches(regex.toRegex()), "String $value should match regex $regex")
  }

  fun haveLength(length: Int): Matcher<String> = object : Matcher<String> {
    override fun test(value: String) = Result(value.length == length, "String $value should have length $length")
  }
}
