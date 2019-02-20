package io.kotlintest.extensions.system

@PublishedApi
internal infix fun Map<String,String>.overridenWith(map: Map<String, String?>): MutableMap<String, String> {
  return toMutableMap().apply { putReplacingNulls(map) }
}

internal fun MutableMap<String,String>.putReplacingNulls(map: Map<String, String?>) {
  map.forEach { key, value ->
    if(value == null) remove(key) else put(key, value)
  }
}