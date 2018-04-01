package io.kotlintest.matchers.string

import io.kotlintest.Matcher
import io.kotlintest.Result

fun containOnlyDigits() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.chars().allMatch { Character.isDigit(it) }
    return Result(passed, "String $value should contain only digits", "String $value should not contain only digits")
  }
}

fun containADigit() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.chars().anyMatch { Character.isDigit(it) }
    return Result(passed, "String $value should contain at least one digits", "String $value should not contain any digits")
  }
}

fun containOnlyOnce(substring: String) = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.indexOf(substring) == value.lastIndexOf(substring)
    return Result(passed, "String $value should contain the substring $substring only once", "String $value should not contain the substring $substring exactly once")
  }
}

fun beLowerCase() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.toLowerCase() == value
    return Result(passed, "String $value should be lower case", "String $value should not should be lower case")
  }
}

fun beUpperCase() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.toUpperCase() == value
    return Result(passed, "String $value should be upper case", "String $value should not should be upper case")
  }
}


fun beEmpty() = object : Matcher<String> {
  override fun test(value: String): Result {
    return Result(value.isEmpty(), "String $value should be empty", "String $value should not be empty")
  }
}

fun haveSameLengthAs(other: String) = object : Matcher<String> {
  override fun test(value: String): Result {
    return Result(value.length == other.length,
        "String $value should have the same length as $other",
        "String $value should not have the same length as $other")
  }
}

fun haveLineCount(count: Int) = object : Matcher<String> {
  override fun test(value: String): Result {
    val lines = value.split(System.lineSeparator()).size
    return Result(lines == count,
        "String $value should have $count lines but had $lines",
        "String $value should not have $count lines")
  }
}

fun containOnlyWhitespace() = beBlank()
fun beBlank() = object : Matcher<String> {
  override fun test(value: String): Result {
    return Result(value.isBlank(),
        "String $value should contain only whitespace",
        "String $value should not contain only whitespace")
  }
}

fun containIgnoringCase(substring: String) = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.toLowerCase().indexOf(substring.toLowerCase()) >= 0
    return Result(passed,
        "String $value should contain the substring $substring (case insensitive)",
        "String $value should not contain the substring $substring (case insensitive)")
  }
}

fun contain(regex: Regex) = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.contains(regex)
    return Result(passed, "String $value should contain regex $regex", "String $value should not contain regex $regex")
  }
}

fun contain(substr: String) = include(substr)
fun include(substr: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String) = Result(value.contains(substr), "String $value should include substring $substr", "String $value should not include substring $substr")
}