package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.extensions.junitxml.JunitXmlReporter
import java.io.File

class ProjectConfig : AbstractProjectConfig() {

   override val specExecutionOrder = SpecExecutionOrder.Annotated

   override fun extensions(): List<Extension> {
      return listOf(
         JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
            outputDir = taskTestResultsDir.resolve("without_containers").invariantSeparatorsPath,
         ),
         JunitXmlReporter(
            includeContainers = true,
            useTestPathAsName = false,
            outputDir = taskTestResultsDir.resolve("with_containers").invariantSeparatorsPath,
         )
      )
   }

   companion object {
      internal val taskTestResultsDir: File by lazy {
         System.getProperty("taskTestResultsDir")
            ?.let { File(it) }
            ?: error("Missing system property 'taskTestResultsDir'")
      }
   }
}
