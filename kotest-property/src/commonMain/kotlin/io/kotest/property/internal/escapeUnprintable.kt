package io.kotest.property.internal

/**
 * Escapes non-printable (ISO control) characters in the string by converting them into their
 * Unicode notation (`U+xxxx` for BMP characters and `U+xxxxxxxx` for supplementary characters).
 * Printable characters remain unchanged.
 *
 * This is useful for displaying strings that may contain unprintable characters, such as control
 * characters, in a more readable and standardized form by replacing those characters with their
 * corresponding Unicode code points.
 *
 * Example:
 * ```
 * val input = "Hello\u0007World"
 * val escaped = input.escapeUnprintable()  // Returns "HelloU+0007World"
 * ```
 *
 * @receiver String The string in which unprintable characters will be escaped.
 * @return A new string with non-printable characters replaced by their Unicode code points.
 */
fun String.escapeUnprintable(): String = buildString {
   this@escapeUnprintable.forEach { c ->
      if (c.isISOControl()) {
         append(
            "U+${
               c.code.toString(16).uppercase().padStart(4, '0')
            }"
         )
      } else {
         append(c)
      }
   }
}
