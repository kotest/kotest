package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds names to show in the run configuration dropdown for tests/specs/packages.
 */
data class GradleTestRunNameBuilder(
   private val spec: KtClassOrObject?,
   private val test: Test?
) {

   companion object {
      fun builder(): GradleTestRunNameBuilder = GradleTestRunNameBuilder(null, null)
   }

   fun withSpec(spec: KtClassOrObject): GradleTestRunNameBuilder {
      return copy(spec = spec)
   }

   fun withTest(test: Test?): GradleTestRunNameBuilder {
      return copy(test = test)
   }

   fun build(): String {
      return buildString {
         append(spec?.name)
         if (test != null) {
            append(" ")
            append(test.readableTestPath())
         }
      }
   }
}
