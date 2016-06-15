package io.kotlintest.matchers

import io.kotlintest.TestFailedException

interface MapMatchers {

  fun <K> haveKey(key: K): Matcher<Map<K, *>> = object : Matcher<Map<K, *>> {
    override fun test(map: Map<K, *>) {
      if (!map.containsKey(key))
        throw TestFailedException("Map did not contain key $key")
    }
  }

  fun <V> haveValue(value: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
    override fun test(map: Map<*, V>) {
      if (!map.containsValue(value))
        throw TestFailedException("Map did not contain value $value")
    }
  }
}