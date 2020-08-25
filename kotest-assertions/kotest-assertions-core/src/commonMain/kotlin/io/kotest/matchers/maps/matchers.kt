package io.kotest.matchers.maps

import io.kotest.fp.getOrElse
import io.kotest.fp.toOption
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <K, V> mapcontain(key: K, v: V) = object : Matcher<Map<K, V>> {
   override fun test(value: Map<K, V>) = MatcherResult(
      value[key] == v,
      "Map should contain mapping $key=$v but was ${buildActualValue(value)}",
      "Map should not contain mapping $key=$v but was $value"
   )

   private fun buildActualValue(map: Map<K, V>) = map[key].toOption().map { "$key=$it" }.getOrElse(map)
}

fun <K, V> Map<K, V>.shouldContain(key: K, value: V) = this should mapcontain(key, value)
fun <K, V> Map<K, V>.shouldNotContain(key: K, value: V) = this shouldNot mapcontain(key, value)

infix fun <K, V> Map<K, V>.shouldContain(entry: Pair<K, V>) = this should mapcontain(entry.first, entry.second)
infix fun <K, V> Map<K, V>.shouldNotContain(entry: Pair<K, V>) = this shouldNot mapcontain(entry.first, entry.second)

infix fun <K, V> Map<K, V>.shouldContainExactly(expected: Map<K, V>) = this should containExactly(expected)
infix fun <K, V> Map<K, V>.shouldNotContainExactly(expected: Map<K, V>) = this shouldNot containExactly(expected)

infix fun <K, V> Map<K, V>.shouldContainAll(expected: Map<K, V>) = this should containAll(expected)
infix fun <K, V> Map<K, V>.shouldNotContainAll(expected: Map<K, V>) = this shouldNot containAll(expected)

infix fun <K, V : Any> Map<K, V>.shouldHaveKey(key: K) = this should haveKey(key)
infix fun <K, V : Any> Map<K, V>.shouldContainKey(key: K) = this should haveKey(key)
infix fun <K, V : Any> Map<K, V>.shouldNotHaveKey(key: K) = this shouldNot haveKey(key)
infix fun <K, V : Any> Map<K, V>.shouldNotContainKey(key: K) = this shouldNot haveKey(key)

infix fun <K, V> Map<K, V>.shouldContainValue(value: V) = this should haveValue<V>(value)
infix fun <K, V> Map<K, V>.shouldNotContainValue(value: V) = this shouldNot haveValue<V>(value)

infix fun <K, V> Map<K, V>.shouldHaveSize(size: Int) = this should haveSize(size)

fun <K, V> Map<K, V>.shouldHaveKeys(vararg keys: K) = this should haveKeys(*keys)
fun <K, V> Map<K, V>.shouldContainKeys(vararg keys: K) = this should haveKeys(*keys)
fun <K, V> Map<K, V>.shouldNotHaveKeys(vararg keys: K) = this shouldNot haveKeys(*keys)
fun <K, V> Map<K, V>.shouldNotContainKeys(vararg keys: K) = this shouldNot haveKeys(*keys)

fun <K, V> Map<K, V>.shouldHaveValues(vararg values: V) = this should haveValues(*values)
fun <K, V> Map<K, V>.shouldContainValues(vararg values: V) = this should haveValues(*values)
fun <K, V> Map<K, V>.shouldNotHaveValues(vararg values: V) = this shouldNot haveValues(*values)
fun <K, V> Map<K, V>.shouldNotContainValues(vararg values: V) = this shouldNot haveValues(*values)

fun <K, V> Map<K, V>.shouldBeEmpty() = this should beEmpty()
fun <K, V> Map<K, V>.shouldNotBeEmpty() = this shouldNot beEmpty()

fun beEmpty() = object : Matcher<Map<*, *>> {
   override fun test(value: Map<*, *>): MatcherResult {
      return MatcherResult(
         value.isEmpty(),
         { "Map should be empty, but was $value." },
         { "Map should not be empty, but was." }
      )
   }
}
