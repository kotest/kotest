package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface MapMatchers {

  fun <K> haveKey(key: K): Matcher<Map<K, *>> = object : Matcher<Map<K, *>> {
    override fun test(value: Map<K, *>) {
      if (!value.containsKey(key))
        throw TestFailedException("Map did not contain key $key")
    }
  }

  fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
    override fun test(value: Map<*, V>) {
      if (!value.containsValue(v))
        throw TestFailedException("Map did not contain value $v")
    }
  }

  fun <K, V> contain(key: K, value: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
    override fun test(value: Map<K, V>) {
      if (value.get(key) != value)
        throw TestFailedException("Map did not contain mapping $key=$value")
    }
  }
}