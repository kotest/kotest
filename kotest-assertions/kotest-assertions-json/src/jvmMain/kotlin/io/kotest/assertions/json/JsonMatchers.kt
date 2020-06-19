package io.kotest.assertions.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

private val mapper by lazy { ObjectMapper().registerKotlinModule() }

infix fun String?.shouldMatchJson(expected: String?) = this should matchJson(expected)
infix fun String?.shouldNotMatchJson(expected: String?) = this shouldNot matchJson(expected)
fun matchJson(expected: String?) = object : Matcher<String?> {

  override fun test(value: String?): MatcherResult {
    val actualJson = value?.let(mapper::readTree)
    val expectedJson = expected?.let(mapper::readTree)

    return MatcherResult(
      actualJson == expectedJson,
      "expected: $expectedJson but was: $actualJson",
      "expected not to match with: $expectedJson but match: $actualJson"
    )
  }
}

@OptIn(ExperimentalContracts::class)
infix fun String?.shouldMatchJsonResource(resource: String) {
  contract {
    returns() implies (this@shouldMatchJsonResource != null)
  }

  this should matchJsonResource(resource)
}

infix fun String.shouldNotMatchJsonResource(resource: String) = this shouldNot matchJsonResource(resource)
fun matchJsonResource(resource: String) = object : Matcher<String?> {

  override fun test(value: String?): MatcherResult {
    val actualJson = value?.let(mapper::readTree)
    val expectedJson = mapper.readTree(this.javaClass.getResourceAsStream(resource))

    return MatcherResult(
      actualJson == expectedJson,
      "expected: $expectedJson but was: $actualJson",
      "expected not to match with: $expectedJson but match: $actualJson"
    )
  }
}

@OptIn(ExperimentalContracts::class)
infix fun String?.shouldContainJsonKey(path: String) {
  contract {
    returns() implies (this@shouldContainJsonKey != null)
  }

  this should containJsonKey(path)
}

infix fun String.shouldNotContainJsonKey(path: String) = this shouldNot containJsonKey(path)
fun containJsonKey(path: String) = object : Matcher<String?> {

  override fun test(value: String?): MatcherResult {
    val sub = when (value) {
      null -> value
      else -> if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."
    }

    val passed = try {
      value != null && JsonPath.read<String>(value, path) != null
    } catch (t: PathNotFoundException) {
      false
    }

    return MatcherResult(
      passed,
      "$sub should contain the path $path",
      "$sub should not contain the path $path"
    )
  }
}

@OptIn(ExperimentalContracts::class)
fun <T> String?.shouldContainJsonKeyValue(path: String, value: T) {
  contract {
    returns() implies (this@shouldContainJsonKeyValue != null)
  }

  this should containJsonKeyValue(path, value)
}

fun <T> String.shouldNotContainJsonKeyValue(path: String, value: T) = this shouldNot containJsonKeyValue(path, value)
fun <T> containJsonKeyValue(path: String, t: T) = object : Matcher<String?> {
  override fun test(value: String?): MatcherResult {
    val sub = when (value) {
      null -> value
      else -> if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."
    }

    return MatcherResult(
      value != null && JsonPath.read<T>(value, path) == t,
      "$sub should contain the element $path = $t",
      "$sub should not contain the element $path = $t"
    )
  }
}
