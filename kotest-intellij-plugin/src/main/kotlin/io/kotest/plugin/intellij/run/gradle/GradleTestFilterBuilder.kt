package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the --tests filter used by Gradle to run a subset of tests.
 */
data class GradleTestFilterBuilder(
   private val spec: KtClassOrObject?,
   private val test: Test?
) {

   companion object {
      fun builder(): GradleTestFilterBuilder = GradleTestFilterBuilder(null, null)
   }

   fun withSpec(spec: KtClassOrObject): GradleTestFilterBuilder {
      return copy(spec = spec)
   }

   fun withTest(test: Test?): GradleTestFilterBuilder {
      return copy(test = test)
   }

   fun build(includeTestsFlag: Boolean): String {
      return buildString {
         if (includeTestsFlag)
            append("--tests ")
         append("'")
         if (spec != null) {
            append(spec.fqName!!.asString())
         }
         if (test != null) {
            append(".")
            append(test.path().joinToString(" -- ") { it.name.escapeSingleQuotes() })
         }
         append("'")
      }
   }
}

/**
 * Escapes single quotes for use inside a single-quoted shell argument.
 *
 * A single quote cannot appear inside a single-quoted string, so we close the
 * quoted string, emit a backslash-escaped single quote, then reopen the quoted
 * string: `'` → `'\''`.
 *
 * For example, the test name `it's a test` becomes `it'\''s a test`, which
 * when wrapped in outer single quotes produces `'it'\''s a test'`.
 */
private fun String.escapeSingleQuotes(): String = replace("'", "'\\''")
