package io.kotlintest.properties

private fun convertValueToString(value: Any?): String = when (value) {
  null -> "<null>"
  "" -> "<empty string>"
  else -> {
    val str = value.toString()
    if (str.isBlank()) "${str.replace("\n", "\\n").replace("\t", "\\t")} <whitespace only>" else str
  }
}

fun <T> shrink(t: T, gen: Gen<T>, test: (T) -> Unit): T {
  println("Attempting to shrink failed value $t")
  var candidate = t
  val shrinker = gen.shrinker()
  if (shrinker == null) return t
  else {
    val tested = HashSet<T>()
    var count = 0
    while (true) {
      val candidates = shrinker.shrink(candidate).filterNot { tested.contains(it) }
      if (candidates.isEmpty()) {
        println("Shrink result: ${convertValueToString(candidate)}")
        return candidate
      } else {
        val next = candidates.firstOrNull {
          tested.add(it)
          count++
          fun whitespace(str: String) = str.isBlank()
          try {
            test(it)
            println("Shrink #$count: ${convertValueToString(it)} pass")
            false
          } catch (t: Throwable) {
            println("Shrink #$count: ${convertValueToString(it)} fail")
            true
          }
        }
        if (next == null) {
          println("Shrink result: ${convertValueToString(candidate)}")
          return candidate
        } else {
          candidate = next
        }
      }
    }
  }
}

fun propertyTestFailureMessage(attempt: Int,
                               inputs: List<PropertyFailureInput<out Any?>>): String {
  val sb = StringBuilder("Property failed for\n")
  inputs.withIndex().forEach {
    val input = if (it.value.shrunk == it.value.original) {
      "${it.index}: ${convertValueToString(it.value.shrunk)}"
    } else {
      "${it.index}: ${convertValueToString(it.value.shrunk)} (shrunk from ${it.value.original})"
    }
    sb.append(input)
    sb.append("\n")
  }
  sb.append("after $attempt attempts")
  return sb.toString()
}

data class PropertyFailureInput<T>(val original: T?, val shrunk: T?)

class PropertyAssertionError(val e: AssertionError,
                             val attempt: Int,
                             val inputs: List<PropertyFailureInput<out Any?>>) : AssertionError(propertyTestFailureMessage(attempt, inputs), e)