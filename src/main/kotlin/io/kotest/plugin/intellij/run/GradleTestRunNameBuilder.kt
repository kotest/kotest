package io.kotest.plugin.intellij.run

import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClass

/**
 * Builds names to show in the run configuration dropdown for tests/specs/packages.
 */
data class GradleTestRunNameBuilder(
   private val spec: KtClass?,
   private val test: Test?
) {

   companion object {
      fun builder(): GradleTestRunNameBuilder = GradleTestRunNameBuilder(null, null)
   }

   fun withSpec(spec: KtClass): GradleTestRunNameBuilder {
      return copy(spec = spec)
   }

   fun withTest(test: Test): GradleTestRunNameBuilder {
      return copy(test = test)
   }

   fun build(): String = "[kotest] ${spec?.name} ${test?.readableTestPath()}"
}
