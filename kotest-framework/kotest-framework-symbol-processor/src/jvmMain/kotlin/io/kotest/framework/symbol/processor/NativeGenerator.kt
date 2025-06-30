package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

class NativeGenerator(private val environment: SymbolProcessorEnvironment) {
   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>) {
      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.runtime.native",
         fileName = "kotest",
         extensionName = "kt"
      )
      outputStream.bufferedWriter().use { writer ->
         writer.write(
            buildString {
               appendLine("""package io.kotest.runtime.native""")
               appendLine()
               appendLine("""import io.kotest.engine.TestEngineLauncher""")
               appendLine("""import io.kotest.core.spec.SpecRef""")
               appendLine("""import kotlin.test.Test""")
               appendLine("""import kotlin.test.AfterClass""")

               specs.forEach {
                  appendLine("""import ${it.qualifiedName?.asString()}""")
               }

               appendLine(
                  """
// we need at least one test otherwise the @AfterClass will not be called
@Test
fun configureKotest() {
}

// we run Kotest after all kotlin.test tests have been executed
@AfterClass
fun runKotest() {
  TestEngineLauncher()
   .withNative()
   .withTeamCityListener()
   .withSpecRefs("""
               )

               specs.forEach {
                  appendLine("""SpecRef.Function({ $it() }, $it::class),""")
               }

               appendLine(
                  """   )
   .launch()
}"""
               )
            }
         )
      }
   }
}
