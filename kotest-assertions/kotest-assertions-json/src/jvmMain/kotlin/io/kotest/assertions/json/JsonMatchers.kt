package io.kotest.assertions.json

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.jayway.jsonpath.JsonPath
import com.jayway.jsonpath.PathNotFoundException
import io.kotest.Matcher
import io.kotest.MatcherResult
import io.kotest.should
import io.kotest.shouldNot

fun String.shouldMatchJson(json: String) = this should matchJson(json)
fun String.shouldNotMatchJson(json: String) = this shouldNot matchJson(json)
fun matchJson(json: String) = object : Matcher<String> {

  val mapper = ObjectMapper().registerModule(KotlinModule())

  override fun test(value: String): MatcherResult {

    val actualJson = mapper.readTree(value)
    val expectedJson = mapper.readTree(json)

    return MatcherResult(
        actualJson == expectedJson,
        "Expected $expectedJson but was $actualJson",
        "Value should not be $expectedJson"
    )
  }
}

fun String.shouldMatchJsonResource(resource: String) = this should matchJsonResource(resource)
fun String.shouldNotMatchJsonResource(resource: String) = this shouldNot matchJsonResource(resource)

fun matchJsonResource(resource: String) = object : Matcher<String> {

  val mapper = ObjectMapper().registerModule(KotlinModule())

  override fun test(value: String): MatcherResult {

    val actualJson = mapper.readTree(value)
    val expectedJson = mapper.readTree(this.javaClass.getResourceAsStream(resource))

    return MatcherResult(
        actualJson == expectedJson,
        "Expected $expectedJson but was $actualJson",
        "Value should not be $expectedJson"
    )
  }
}

fun String.shouldContainJsonKey(path: String) = this should containJsonKey(path)
fun String.shouldNotContainJsonKey(path: String) = this shouldNot containJsonKey(path)
fun containJsonKey(path: String) = object : Matcher<String> {

  override fun test(value: String): MatcherResult {

    val sub = if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."

    val passed = try {
      JsonPath.read<String>(value, path) != null
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

fun <T> String.shouldContainJsonKeyValue(path: String, value: T) = this should containJsonKeyValue(path, value)
fun <T> String.shouldNotContainJsonKeyValue(path: String, value: T) = this shouldNot containJsonKeyValue(path, value)
fun <T> containJsonKeyValue(path: String, t: T) = object : Matcher<String> {
  override fun test(value: String): MatcherResult {
    val sub = if (value.length < 50) value.trim() else value.substring(0, 50).trim() + "..."
    return MatcherResult(
        JsonPath.read<T>(value, path) == t,
        "$sub should contain the element $path = $t",
        "$sub should not contain the element $path = $t"
    )
  }
}
