package io.kotest.engine.config

import io.kotest.core.config.AbstractProjectConfig

internal object AbstractProjectConfigWriter {

   fun dumpProjectConfig(projectConfig: AbstractProjectConfig) {
      println("~~~ Kotest Configuration ~~~")
      println(createConfigSummary(projectConfig))
   }

   internal fun createConfigSummary(projectConfig: AbstractProjectConfig): String {

      val sb = StringBuilder()

      projectConfig.specExecutionMode?.let { sb.buildOutput("Spec execution mode", it::class.simpleName) }
      projectConfig.testExecutionMode?.let { sb.buildOutput("Test execution mode", it::class.simpleName) }

      projectConfig.coroutineDebugProbes?.let { sb.buildOutput("Coroutine debug probe", it.toString()) }
      projectConfig.specExecutionOrder?.let { sb.buildOutput("Spec execution order", it.name) }
      projectConfig.testCaseOrder?.let { sb.buildOutput("Test case order", it.name) }

      projectConfig.timeout?.let { sb.buildOutput("Default test timeout", it.toString()) }
      projectConfig.invocationTimeout?.let { sb.buildOutput("Default test invocation timeout", it.toString()) }

      projectConfig.projectTimeout?.let { sb.buildOutput("Project timeout", it.toString()) }
      projectConfig.isolationMode?.let { sb.buildOutput("Default isolation mode", it.name) }

      projectConfig.globalAssertSoftly?.let { sb.buildOutput("Global soft assertions", it.toString()) }
      projectConfig.writeSpecFailureFile?.let { sb.buildOutput("Write spec failure file", it.toString()) }

      projectConfig.failOnIgnoredTests?.let { sb.buildOutput("Fail on ignored tests", it.toString()) }
      projectConfig.failOnEmptyTestSuite?.let { sb.buildOutput("Fail on empty test suite", it.toString()) }

      projectConfig.duplicateTestNameMode?.let { sb.buildOutput("Duplicate test name mode", it.name) }

      projectConfig.includeTestScopeAffixes?.let { sb.buildOutput("Include test scope affixes", it.toString()) }

      projectConfig.removeTestNameWhitespace?.let { sb.buildOutput("Remove test name whitespace", it.toString()) }
      projectConfig.testNameAppendTags?.let { sb.buildOutput("Append tags to test names", it.toString()) }

//   if (registry.isNotEmpty()) {
//      sb.buildOutput("Extensions")
//      registry.all().map(::mapClassName).forEach {
//         sb.buildOutput(it, indentation = 1)
//      }
//   }

//   runtimeTagExpression().expression.let { sb.buildOutput("Tags", it) }
      return sb.toString()
   }

   private fun StringBuilder.buildOutput(key: String, value: String? = null, indentation: Int = 0) {
      if (indentation == 0) {
         append("-> ")
      } else {
         (0 until indentation).forEach { i ->
            append("  ")
         }
         append("- ")
      }
      append(key)
      value?.let { append(": $it") }
      append("\n")
   }

}
