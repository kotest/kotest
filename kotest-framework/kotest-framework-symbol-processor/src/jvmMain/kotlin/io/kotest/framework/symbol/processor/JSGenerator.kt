package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

class JSGenerator(private val environment: SymbolProcessorEnvironment) {
   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>) {
      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.js",
         fileName = "kotest",
         extensionName = "kt"
      )
      outputStream.bufferedWriter().use { writer ->
         writer.write(
            buildString {
               appendLine("""package io.kotest.framework.runtime.js""")
               appendLine()
               appendLine("""import io.kotest.engine.TestEngineLauncher""")
               appendLine("""import io.kotest.core.spec.SpecRef""")
               appendLine("""import io.kotest.engine.extensions.ProvidedDescriptorFilter""")
               appendLine("""import io.kotest.core.descriptors.DescriptorPaths""")

               specs.forEach {
                  appendLine("""import ${it.qualifiedName?.asString()}""")
               }

               appendLine(
                  """
@OptIn(ExperimentalJsExport::class)
@JsExport
fun runKotest(listenerType: String, descriptorArg: String?) {

  val descriptor = descriptorArg?.let { DescriptorPaths.parse(it) }
  val filter = descriptor?.let { ProvidedDescriptorFilter(descriptor) }

  val launcher = TestEngineLauncher()
    .withJs()
    .addExtensions(listOfNotNull(filter))
    .withSpecRefs("""
               )
               specs.forEach {
                  appendLine("""      SpecRef.Function ({ $it() }, $it::class), """)
               }
               appendLine(
                  """    )
  when (listenerType) {
      "teamcity" -> launcher.withTeamCityListener().promise()
      else -> launcher.promise()
   }
}"""
               )
            }
         )
      }
   }
}
