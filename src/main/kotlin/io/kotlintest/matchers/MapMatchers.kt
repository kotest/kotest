package io.kotlintest.matchers


fun <K> haveKey(key: K): Matcher<Map<K, *>> = object : Matcher<Map<K, *>> {
  override fun test(value: Map<K, *>) = Result(value.containsKey(key), "Map should contain key $key")
}

fun <V> haveValue(v: V): Matcher<Map<*, V>> = object : Matcher<Map<*, V>> {
  override fun test(value: Map<*, V>) = Result(value.containsValue(v), "Map should contain value $v")
}

fun <K, V> contain(key: K, v: V): Matcher<Map<K, V>> = object : Matcher<Map<K, V>> {
  override fun test(value: Map<K, V>) = Result(value[key] == v, "Map should contain mapping $key=$v but was $value")
}