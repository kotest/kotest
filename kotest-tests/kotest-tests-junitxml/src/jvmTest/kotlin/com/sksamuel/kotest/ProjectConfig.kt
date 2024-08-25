package com.sksamuel.kotest

import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.extensions.Extension
import io.kotest.core.spec.SpecExecutionOrder
import io.kotest.extensions.junitxml.JunitXmlReporter
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.absolute
import kotlin.io.path.invariantSeparatorsPathString
import kotlin.io.path.relativeTo

class ProjectConfig : AbstractProjectConfig() {

   override val specExecutionOrder = SpecExecutionOrder.Annotated

   override fun extensions(): List<Extension> {
      val outputDir = taskTestResultsDir.relativeTo(buildDir).invariantSeparatorsPathString

      return listOf(
         JunitXmlReporter(
            includeContainers = false,
            useTestPathAsName = true,
            outputDir = "$outputDir/without_containers",
         ),
         JunitXmlReporter(
            includeContainers = true,
            useTestPathAsName = false,
            outputDir = "$outputDir/with_containers",
         )
      )
   }

   companion object {
      private val buildDir = Path("./build").absolute().normalize()

      internal val taskTestResultsDir: Path by lazy {
         System.getProperty("taskTestResultsDir")
            ?.let { Path(it) }
            ?: error("Missing system property 'taskTestResultsDir'")
      }
   }
}
