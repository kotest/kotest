package io.kotest.matchers.maps

import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot

fun <K, V> Map<K, V>.shouldContain(key: K, value: V): Map<K, V> {
   this should mapcontain(key, value)
   return this
}

fun <K, V> Map<K, V>.shouldNotContain(key: K, value: V): Map<K, V> {
   this shouldNot mapcontain(key, value)
   return this
}

infix fun <K, V> Map<K, V>.shouldContain(entry: Pair<K, V>): Map<K, V> {
   this should mapcontain(entry.first, entry.second)
   return this
}

infix fun <K, V> Map<K, V>.shouldNotContain(entry: Pair<K, V>): Map<K, V> {
   this shouldNot mapcontain(entry.first, entry.second)
   return this
}

infix fun <K, V> Map<K, V>.shouldContainExactly(expected: Map<K, V>): Map<K, V> {
   this should containExactly(expected)
   return this
}

infix fun <K, V> Map<K, V>.shouldNotContainExactly(expected: Map<K, V>): Map<K, V> {
   this shouldNot containExactly(expected)
   return this
}

infix fun <K, V> Map<K, V>.shouldContainAll(expected: Map<K, V>): Map<K, V> {
   this should containAll(expected)
   return this
}
infix fun <K, V> Map<K, V>.shouldNotContainAll(expected: Map<K, V>): Map<K, V> {
   this shouldNot containAll(expected)
   return this
}

infix fun <K, V : Any> Map<K, V>.shouldHaveKey(key: K): Map<K, V> {
   this should haveKey(key)
   return this
}
infix fun <K, V : Any> Map<K, V>.shouldContainKey(key: K): Map<K, V> {
   this should haveKey(key)
   return this
}
infix fun <K, V : Any> Map<K, V>.shouldNotHaveKey(key: K): Map<K, V> {
   this shouldNot haveKey(key)
   return this
}
infix fun <K, V : Any> Map<K, V>.shouldNotContainKey(key: K): Map<K, V> {
   this shouldNot haveKey(key)
   return this
}

infix fun <K, V> Map<K, V>.shouldContainValue(value: V): Map<K, V> {
   this should haveValue<V>(value)
   return this
}
infix fun <K, V> Map<K, V>.shouldNotContainValue(value: V): Map<K, V> {
   this shouldNot haveValue<V>(value)
   return this
}

infix fun <K, V> Map<K, V>.shouldHaveSize(size: Int): Map<K, V> {
   this should haveSize(size)
   return this
}

fun <K, V> Map<K, V>.shouldHaveKeys(vararg keys: K): Map<K, V> {
   this should haveKeys(*keys)
   return this
}
fun <K, V> Map<K, V>.shouldContainKeys(vararg keys: K): Map<K, V> {
   this should haveKeys(*keys)
   return this
}
fun <K, V> Map<K, V>.shouldContainAnyKeysOf(vararg keys: K): Map<K, V> {
   this should containAnyKeys(*keys)
   return this
}
fun <K, V> Map<K, V>.shouldNotHaveKeys(vararg keys: K): Map<K, V> {
   this shouldNot haveKeys(*keys)
   return this
}
fun <K, V> Map<K, V>.shouldNotContainKeys(vararg keys: K): Map<K, V> {
   this shouldNot haveKeys(*keys)
   return this
}
fun <K, V> Map<K, V>.shouldNotContainAnyKeysOf(vararg keys: K): Map<K, V> {
   this shouldNot containAnyKeys(*keys)
   return this
}

fun <K, V> Map<K, V>.shouldHaveValues(vararg values: V): Map<K, V> {
   this should haveValues(*values)
   return this
}
fun <K, V> Map<K, V>.shouldContainValues(vararg values: V): Map<K, V> {
   this should haveValues(*values)
   return this
}
fun <K, V> Map<K, V>.shouldContainAnyValuesOf(vararg values: V): Map<K, V> {
   this should containAnyValues(*values)
   return this
}
fun <K, V> Map<K, V>.shouldNotHaveValues(vararg values: V): Map<K, V> {
   this shouldNot haveValues(*values)
   return this
}
fun <K, V> Map<K, V>.shouldNotContainValues(vararg values: V): Map<K, V> {
   this shouldNot haveValues(*values)
   return this
}
fun <K, V> Map<K, V>.shouldNotContainAnyValuesOf(vararg values: V): Map<K, V> {
   this shouldNot containAnyValues(*values)
   return this
}

fun <K, V> Map<K, V>.shouldBeEmpty(): Map<K, V> {
   this should beEmpty()
   return this
}
fun <K, V> Map<K, V>.shouldNotBeEmpty(): Map<K, V> {
   this shouldNot beEmpty()
   return this
}

fun beEmpty() = object : Matcher<Map<*, *>> {
   override fun test(value: Map<*, *>): MatcherResult {
      return MatcherResult(
         value.isEmpty(),
         { "Map should be empty, but was $value." },
         { "Map should not be empty, but was." }
      )
   }
}


fun <K, V> Map<K, V>.shouldMatchAll(vararg matchers: Pair<K, (V) -> Unit>): Map<K, V> {
   this should matchAll(*matchers)
   return this
}
infix fun <K, V> Map<K, V>.shouldMatchAll(expected: Map<K, (V) -> Unit>): Map<K, V> {
   this should matchAll(expected)
   return this
}
fun <K, V> Map<K, V>.shouldNotMatchAll(vararg matchers: Pair<K, (V) -> Unit>): Map<K, V> {
   this shouldNot matchAll(*matchers)
   return this
}
infix fun <K, V> Map<K, V>.shouldNotMatchAll(expected: Map<K, (V) -> Unit>): Map<K, V> {
   this shouldNot matchAll(expected)
   return this
}
fun <K, V> Map<K, V>.shouldMatchExactly(vararg matchers: Pair<K, (V) -> Unit>): Map<K, V> {
   this should matchExactly(*matchers)
   return this
}
infix fun <K, V> Map<K, V>.shouldMatchExactly(expected: Map<K, (V) -> Unit>): Map<K, V> {
   this should matchExactly(expected)
   return this
}
fun <K, V> Map<K, V>.shouldNotMatchExactly(vararg matchers: Pair<K, (V) -> Unit>): Map<K, V> {
   this shouldNot matchExactly(*matchers)
   return this
}

infix fun <K, V> Map<K, V>.shouldNotMatchExactly(expected: Map<K, (V) -> Unit>): Map<K, V> {
   this shouldNot matchExactly(expected)
   return this
}
