package io.kotest.plugin.intellij.run

import com.intellij.openapi.module.Module
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.gradle.GradleUtils
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

   @Suppress("UnstableApiUsage")
   fun build(): List<String> {
      // the debug settings do not propagate from kotest to the other test tasks yet,
      // so if we think we're in a jvm test we can just invoke jvmKotest directly, and then debug will work
      val task = if (module.name.endsWith(".test")) {
         val tasks = GradleUtils.listTasks(module).map { it.getFqnTaskName() }
         if (tasks.any { it.endsWith(":jvmKotest") }) "jvmKotest" else "kotest"
      } else "kotest"
      return listOfNotNull(task, includeArg())
   }

   private fun includeArg(): String? {
      return when (test) {
         null -> "$PROPERTY_INCLUDE='${spec.fqName?.asString()}'"
         else -> "$PROPERTY_INCLUDE='${test.descriptorPath()}'"
      }
   }
}
