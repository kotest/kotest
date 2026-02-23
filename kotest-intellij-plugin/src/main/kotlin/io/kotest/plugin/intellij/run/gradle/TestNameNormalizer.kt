package io.kotest.plugin.intellij.run.gradle

/**
 * Normalizes test names for use in Gradle test filters.
 */
internal object TestNameNormalizer {

   /**
    * Strips newlines and trims surrounding whitespace from a test name.
    *
    * Newlines are replaced with a single space so that multi-line test names
    * produce a single-line filter string that matches the normalized descriptor
    * path used by [GradleClassMethodRegexTestFilter].
    */
   fun normalize(name: String): String =
      name.replace("\r\n", " ").replace("\n", " ").replace("\r", " ").trim()

   /**
    * Normalizes a test name and escapes single quotes for use inside a
    * single-quoted shell argument.
    *
    * A single quote cannot appear inside a single-quoted string, so we close
    * the quoted string, emit a backslash-escaped single quote, then reopen the
    * quoted string: `'` → `'\''`.
    *
    * For example, `it's a test` becomes `it'\''s a test`, which when wrapped
    * in outer single quotes produces `'it'\''s a test'`.
    */
   fun normalizeAndEscape(name: String): String =
      normalize(name).replace("'", "'\\''")
}
