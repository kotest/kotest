package io.kotlintest.matchers.maps

import io.kotlintest.Matcher
import io.kotlintest.MatcherResult
import io.kotlintest.matchers.containAll
import io.kotlintest.matchers.containExactly
import io.kotlintest.matchers.haveKey
import io.kotlintest.matchers.haveKeys
import io.kotlintest.matchers.haveValue
import io.kotlintest.matchers.haveValues
import io.kotlintest.should
import io.kotlintest.shouldNot

fun <K, V> mapcontain(key: K, v: V) = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = MatcherResult(value[key] == v, "Map should contain mapping $key=$v but was $value", "Map should not contain mapping $key=$v but was $value")
}

fun <K, V> Map<K, V>.shouldContain(key: K, value: V) = this should mapcontain(key, value)
fun <K, V> Map<K, V>.shouldNotContain(key: K, value: V) = this shouldNot mapcontain(key, value)

infix fun <K, V> Map<K, V>.shouldContain(entry: Pair<K, V>) = this should mapcontain(entry.first, entry.second)
infix fun <K, V> Map<K, V>.shouldNotContain(entry: Pair<K, V>) = this shouldNot mapcontain(entry.first, entry.second)

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
