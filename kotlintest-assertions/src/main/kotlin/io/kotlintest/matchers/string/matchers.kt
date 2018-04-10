package io.kotlintest.matchers.string

import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot

fun String.shouldContainOnlyDigits() = this should containOnlyDigits()
fun String.shouldNotContainOnlyDigits() = this shouldNot containOnlyDigits()
fun containOnlyDigits() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.chars().allMatch { Character.isDigit(it) }
    return Result(passed, "String $value should contain only digits", "String $value should not contain only digits")
  }
}

fun String.shouldContainADigit() = this should containADigit()
fun String.shouldNotContainADigit() = this shouldNot containADigit()
fun containADigit() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.chars().anyMatch { Character.isDigit(it) }
    return Result(passed, "String $value should contain at least one digits", "String $value should not contain any digits")
  }
}

fun String.shouldContainOnlyOnce(substr: String) = this should containOnlyOnce(substr)
fun String.shouldNotContainOnlyOnce(substr: String) = this shouldNot containOnlyOnce(substr)
fun containOnlyOnce(substring: String) = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.indexOf(substring) == value.lastIndexOf(substring)
    return Result(passed, "String $value should contain the substring $substring only once", "String $value should not contain the substring $substring exactly once")
  }
}

fun String.shouldBeLowerCase() = this should beLowerCase()
fun String.shouldNotBeLowerCase() = this shouldNot beLowerCase()
fun beLowerCase() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.toLowerCase() == value
    return Result(passed, "String $value should be lower case", "String $value should not should be lower case")
  }
}

fun String.shouldBeUpperCase() = this should beUpperCase()
fun String.shouldNotBeUpperCase() = this shouldNot beUpperCase()
fun beUpperCase() = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.toUpperCase() == value
    return Result(passed, "String $value should be upper case", "String $value should not should be upper case")
  }
}

fun String.shouldBeEmpty() = this should beEmpty()
fun String.shouldNotBeEmpty() = this shouldNot beEmpty()
fun beEmpty() = object : Matcher<String> {
  override fun test(value: String): Result {
    return Result(value.isEmpty(), "String $value should be empty", "String $value should not be empty")
  }
}

fun String.shouldHaveSameLengthAs(other: String) = this should haveSameLengthAs(other)
fun String.shouldNotHaveSameLengthAs(other: String) = this shouldNot haveSameLengthAs(other)
fun haveSameLengthAs(other: String) = object : Matcher<String> {
  override fun test(value: String): Result {
    return Result(value.length == other.length,
        "String $value should have the same length as $other",
        "String $value should not have the same length as $other")
  }
}

fun String.shouldHaveLineCount(count: Int) = this should haveLineCount(count)
fun String.shouldNotHaveLineCount(count: Int) = this shouldNot haveLineCount(count)
fun haveLineCount(count: Int) = object : Matcher<String> {
  override fun test(value: String): Result {
    val lines = value.split(System.lineSeparator()).size
    return Result(lines == count,
        "String $value should have $count lines but had $lines",
        "String $value should not have $count lines")
  }
}

fun String.shouldBeBlank() = this should beBlank()
fun String.shouldNotBeBlank() = this shouldNot beBlank()
fun containOnlyWhitespace() = beBlank()
fun beBlank() = object : Matcher<String> {
  override fun test(value: String): Result {
    return Result(value.isBlank(),
        "String $value should contain only whitespace",
        "String $value should not contain only whitespace")
  }
}

fun String.shouldContainIgnoringCase(substr: String) = this should containIgnoringCase(substr)
fun String.shouldNotContainIgnoringCase(substr: String) = this shouldNot containIgnoringCase(substr)
fun containIgnoringCase(substr: String) = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.toLowerCase().indexOf(substr.toLowerCase()) >= 0
    return Result(passed,
        "String $value should contain the substring $substr (case insensitive)",
        "String $value should not contain the substring $substr (case insensitive)")
  }
}

fun String.shouldContain(regex: Regex) = this should contain(regex)
fun String.shouldNotContain(regex: Regex) = this shouldNot contain(regex)
fun contain(regex: Regex) = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.contains(regex)
    return Result(passed, "String $value should contain regex $regex", "String $value should not contain regex $regex")
  }
}

fun String.shouldContain(substr: String) = this should contain(substr)
fun String.shouldNotContain(substr: String) = this shouldNot contain(substr)
fun contain(substr: String) = include(substr)

fun String.shouldInclude(substr: String) = this should include(substr)
fun String.shouldNotInclude(substr: String) = this shouldNot include(substr)
fun include(substr: String): Matcher<String> = object : Matcher<String> {
  override fun test(value: String): Result {
    val passed = value.contains(substr)
    return Result(passed, "String $value should include substring $substr", "String $value should not include substring $substr")
  }
}