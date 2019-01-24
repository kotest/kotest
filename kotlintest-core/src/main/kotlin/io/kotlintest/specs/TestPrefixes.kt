package io.kotlintest.specs

internal fun createTestName(prefix: String, name: String): String {
  return if(name.startsWith("!")) "!$prefix${name.drop(1)}" else "$prefix$name"
}
