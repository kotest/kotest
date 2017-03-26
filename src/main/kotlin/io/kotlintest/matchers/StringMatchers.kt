package io.kotlintest.matchers

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

fun haveSubstring(substr: String) = include(substr)
fun substring(substr: String) = include(substr)
fun include(substr: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.contains(substr), "String $value should include substring $substr")
}

fun endWith(suffix: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.endsWith(suffix), "String $value should end with $suffix")
}

fun match(regex: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.matches(regex.toRegex()), "String $value should match regex $regex")
}

fun strlen(length: Int): Matcher<String> = haveLength(length)
fun haveLength(length: Int): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.length == length, "String $value should have length $length")
}
