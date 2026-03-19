package io.kotest.plugin.intellij.run.gradle

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds names to show in the run configuration dropdown for tests/specs/packages.
 *
 * The name will appear in two places - it will be in the run icon chooser in the gutter Run/Debug/Profile etc.,
 * and will also be the name of the configuration in the run configs drop down
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

   // kotlin.test uses 'class name.method name', so we'll do the same for consistency
   fun build(): String {
      return buildString {
         if (spec != null) {
            append(spec.name)
         }
         if (test != null) {
            append(".")
            append(test.readableTestPath())
         }
      }
   }
}
