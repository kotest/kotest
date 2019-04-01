package io.kotlintest.matchers.maps

import io.kotlintest.Diff
import io.kotlintest.Matcher
import io.kotlintest.Result
import io.kotlintest.should
import io.kotlintest.shouldNot
import io.kotlintest.stringify

fun <K, V> mapcontain(key: K, v: V) = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = Result(value[key] == v,
      "Map should contain mapping $key=$v but was $value",
      "Map should not contain mapping $key=$v but was $value")
}

fun <K, V> Map<K, V>.shouldContain(key: K, value: V) = this should mapcontain(key, value)
fun <K, V> Map<K, V>.shouldNotContain(key: K, value: V) = this shouldNot mapcontain(key, value)

infix fun <K, V> Map<K, V>.shouldContainExactly(expected: Map<K, V>) = this should containExactly(expected)
infix fun <K, V> Map<K, V>.shouldNotContainExactly(expected: Map<K, V>) = this shouldNot containExactly(expected)

infix fun <K, V> Map<K, V>.shouldContainAll(expected: Map<K, V>) = this should containAll(expected)
infix fun <K, V> Map<K, V>.shouldNotContainAll(expected: Map<K, V>) = this shouldNot containAll(expected)

infix fun <K, V> Map<K, V>.shouldContainKey(key: K) = this should haveKey(key)
infix fun <K, V> Map<K, V>.shouldNotContainKey(key: K) = this shouldNot haveKey(key)

infix fun <K, V> Map<K, V>.shouldContainValue(value: V) = this should haveValue(value)
infix fun <K, V> Map<K, V>.shouldNotContainValue(value: V) = this shouldNot haveValue(value)

fun <K, V> Map<K, V>.shouldContainKeys(vararg keys: K) = this should haveKeys(*keys)
fun <K, V> Map<K, V>.shouldNotContainKeys(vararg keys: K) = this shouldNot haveKeys(*keys)

fun <K, V> Map<K, V>.shouldContainValues(vararg values: V) = this should haveValues(*values)
fun <K, V> Map<K, V>.shouldNotContainValues(vararg values: V) = this shouldNot haveValues(*values)



fun <K> haveKey(key: K): Matcher<Map<out K, *>> = object : Matcher<Map<out K, *>> {
  override fun test(value: Map<out K, *>) = Result(value.containsKey(key),
          "Map should contain key $key",
          "Map should not contain key $key")
}

fun <K> haveKeys(vararg keys: K): Matcher<Map<K, *>> = object : Matcher<Map<K, *>> {
  override fun test(value: Map<K, *>): Result {
    val passed = keys.all { value.containsKey(it) }
    return Result(passed,
            "Map did not contain the keys ${keys.joinToString(", ")}",
            "Map should not contain the keys ${keys.joinToString(", ")}")
  }
}

fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>) = Result(value.containsValue(v),
          "Map should contain value $v",
          "Map should not contain value $v")
}

fun <V> haveValues(vararg values: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>): Result {
    val passed = values.all { value.containsValue(it) }
    return Result(passed,
            "Map did not contain the values ${values.joinToString(", ")}",
            "Map should not contain the values ${values.joinToString(", ")}")
  }
}

fun <K, V> contain(key: K, v: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = Result(value[key] == v,
          "Map should contain mapping $key=$v but was $value",
          "Map should not contain mapping $key=$v but was $value")
}

fun <K, V> containAll(expected: Map<K, V>): Matcher<Map<K, V>> =
        MapContainsMatcher(expected, ignoreExtraKeys = true)

fun <K, V> containExactly(expected: Map<K, V>): Matcher<Map<K, V>> =
        MapContainsMatcher(expected)

class MapContainsMatcher<K, V>(
        private val expected: Map<K, V>,
        private val ignoreExtraKeys: Boolean = false
) : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>): Result {
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
    return Result(
            diff.isEmpty(), failureMsg, negatedFailureMsg
    )
  }
}