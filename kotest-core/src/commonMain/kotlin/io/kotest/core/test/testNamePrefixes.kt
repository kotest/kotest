package io.kotest.core.test

/**
 * Creates a test name correctly handling focus and bang. If a prefix is specified,
 * the focus/bang is moved to before the prefix.
 *
 * This means the user writes when("!disable") and the platform invokes ("when", "!disable")
 * and ends up with !when disable, so that it is correctly parsed by the test runtime.
 */
fun createTestName(prefix: String, name: String): String {
   return when {
      name.startsWith("!") -> "!$prefix${name.drop(1)}"
      name.startsWith("f:") -> "f:$prefix${name.drop(2)}"
      else -> "$prefix$name"
   }
}
