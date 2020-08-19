package io.kotest.matchers.maps

import io.kotest.fp.getOrElse
import io.kotest.fp.toOption
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.string.Diff
import io.kotest.matchers.string.stringify

fun <K> haveKey(key: K): Matcher<Map<K, Any?>> = object : Matcher<Map<K, Any?>> {
  override fun test(value: Map<K, Any?>) = MatcherResult(value.containsKey(key),
    "Map should contain key $key",
    "Map should not contain key $key")
}

fun <K> haveKeys(vararg keys: K): Matcher<Map<K, Any?>> = object : Matcher<Map<K, Any?>> {
  override fun test(value: Map<K, Any?>): MatcherResult {
    val passed = keys.all { value.containsKey(it) }
    return MatcherResult(passed,
      "Map did not contain the keys ${keys.joinToString(", ")}",
      "Map should not contain the keys ${keys.joinToString(", ")}")
  }
}

fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>) = MatcherResult(value.containsValue(v),
    "Map should contain value $v",
    "Map should not contain value $v")
}

fun <V> haveValues(vararg values: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>): MatcherResult {
    val passed = values.all { value.containsValue(it) }
    return MatcherResult(passed,
      "Map did not contain the values ${values.joinToString(", ")}",
      "Map should not contain the values ${values.joinToString(", ")}")
  }
}

fun <K, V> contain(key: K, v: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = MatcherResult(value[key] == v,
    "Map should contain mapping $key=$v but was ${buildActualValue(value)}",
    "Map should not contain mapping $key=$v but was $value")

   private fun buildActualValue(map: Map<K, V>) = map[key].toOption().map { "$key=$it" }.getOrElse(map)
}

fun <K, V> containAll(expected: Map<K, V>): Matcher<Map<K, V>> =
  MapContainsMatcher(expected, ignoreExtraKeys = true)

fun <K, V> containExactly(expected: Map<K, V>): Matcher<Map<K, V>> =
  MapContainsMatcher(expected)

fun <K, V> haveSize(size: Int): Matcher<Map<K,V>> = object : Matcher<Map<K, V>> {
   override fun test(value: Map<K, V>) =
      MatcherResult(
         value.size == size,
         { "Map should have size $size but has size ${value.size}" },
         { "Map should not have size $size" }
      )
}

class MapContainsMatcher<K, V>(
  private val expected: Map<K, V>,
  private val ignoreExtraKeys: Boolean = false
) : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>): MatcherResult {
    val diff = Diff.create(value, expected, ignoreExtraMapKeys = ignoreExtraKeys)
    val (expectMsg, negatedExpectMsg) = if (ignoreExtraKeys) {
      "should contain all of" to "should not contain all of"
    } else {
      "should be equal to" to "should not be equal to"
    }
    val (butMsg, negatedButMsg) = if (ignoreExtraKeys) {
      "but differs by" to "but contains"
    } else {
      "but differs by" to "but equals"
    }
    val failureMsg = """
      |
      |Expected:
      |  ${stringify(value)}
      |$expectMsg:
      |  ${stringify(expected)}
      |$butMsg:
      |${diff.toString(1)}
      |
      """.trimMargin()
    val negatedFailureMsg = """
      |
      |Expected:
      |  ${stringify(value)}
      |$negatedExpectMsg:
      |  ${stringify(expected)}
      |$negatedButMsg
      |
    """.trimMargin()
    return MatcherResult(
      diff.isEmpty(), failureMsg, negatedFailureMsg
    )
  }
}
