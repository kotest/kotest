package io.kotlintest.assertions

import java.util.*

fun <T> withSystemProperty(key: String, value: String?, thunk: () -> T): T {
  val previous = System.setProperty(key, value)
  try {
    return thunk()
  } finally {
    if (previous == null)
      System.clearProperty(key)
    else
      System.setProperty(key, previous)
  }
}

fun <T> withSystemProperties(props: List<Pair<String, String?>>, thunk: () -> T): T {
  return withSystemProperties(props.toMap(), thunk)
}

fun <T> withSystemProperties(props: Properties, thunk: () -> T): T {
  val pairs = props.toList().map { it.first.toString() to it.second?.toString() }
  return withSystemProperties(pairs, thunk)
}

fun <T> withSystemProperties(props: Map<String, String?>, thunk: () -> T): T {
  val prevs = props.map { it.key to System.setProperty(it.key, it.value) }
  try {
    return thunk()
  } finally {
    prevs.forEach {
      if (it.second == null)
        System.clearProperty(it.first)
      else
        System.setProperty(it.first, it.second)
    }
  }
}