package io.kotlintest.matchers

import io.kotlintest.Matcher
import io.kotlintest.Result

fun <K> haveKey(key: K): Matcher<Map<out K, *>> = object : Matcher<Map<out K, *>> {
  override fun test(value: Map<out K, *>) = Result(value.containsKey(key), "Map should contain key $key", "Map should not contain key $key")
}

fun <K> haveKeys(vararg keys: K): Matcher<Map<K, *>> = object : Matcher<Map<K, *>> {
  override fun test(value: Map<K, *>): Result {
    val passed = keys.all { value.containsKey(it) }
    return Result(passed, "Map did not contain the keys ${keys.joinToString(", ")}", "Map should not contain the keys ${keys.joinToString(", ")}")
  }
}

fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>) = Result(value.containsValue(v), "Map should contain value $v", "Map should not contain value $v")
}

fun <V> haveValues(vararg values: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>): Result {
    val passed = values.all { value.containsValue(it) }
    return Result(passed, "Map did not contain the values ${values.joinToString(", ")}", "Map should not contain the values ${values.joinToString(", ")}")
  }
}

fun <K, V> contain(key: K, v: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = Result(value[key] == v, "Map should contain mapping $key=$v but was $value", "Map should not contain mapping $key=$v but was $value")
}

fun <K, V> containAll(expected: Map<K, V>): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>): Result {
    val passed = expected.all { value[it.key] == it.value }
    return Result(passed, "Map did not contain all of $expected", "Map should not contain all of $expected")
  }
}