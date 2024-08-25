package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.extensions.junitxml.JunitXmlReporter
import java.nio.file.Path
import kotlin.io.path.Path

class ProjectConfig : AbstractProjectConfig() {

   override val specExecutionOrder = SpecExecutionOrder.Annotated

   override fun extensions(): List<Extension> {
      return listOf(
         JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
            outputDir = taskTestResultsDir,
         ),
         JunitXmlReporter(
            includeContainers = true,
            useTestPathAsName = false,
            outputDir = taskTestResultsDir,
         )
      )
   }

   companion object {
      internal val taskTestResultsDir: Path by lazy {
         System.getProperty("taskTestResultsDir")
            ?.let { Path(it) }
            ?: error("Missing system property 'taskTestResultsDir'")
      }
   }
}
