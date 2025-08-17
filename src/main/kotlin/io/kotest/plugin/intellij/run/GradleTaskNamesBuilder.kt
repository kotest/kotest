package io.kotest.plugin.intellij.run

import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * Builds the gradle command line to execute a kotest test.
 */
data class GradleTaskNamesBuilder(
   private val module: Module,
   private val spec: KtClassOrObject,
   private val test: Test?,
) {

   companion object {
      const val PROPERTY_INCLUDE = "-Pkotest.include"

      fun builder(module: Module, spec: KtClassOrObject): GradleTaskNamesBuilder =
         GradleTaskNamesBuilder(module, spec, null)
   }

   fun withTest(test: Test?): GradleTaskNamesBuilder {
      return copy(test = test)
   }

   fun build(): List<String> {
      return listOfNotNull("kotest", includeArg())
   }

   private fun includeArg(): String? {
      return when (test) {
         null -> "$PROPERTY_INCLUDE='${spec.fqName?.asString()}'"
         else -> "$PROPERTY_INCLUDE='${test.descriptorPath()}'"
      }
   }
}
