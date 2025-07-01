package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile

class NativeGenerator(private val environment: SymbolProcessorEnvironment) {
   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>) {
      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.native",
         fileName = "kotest",
         extensionName = "kt"
      )
      outputStream.bufferedWriter().use { writer ->
         writer.write(
            buildString {
               appendLine("""package io.kotest.framework.runtime.native""")
               appendLine()
               appendLine("""import io.kotest.core.descriptors.DescriptorPaths""")
               appendLine("""import io.kotest.engine.extensions.ProvidedDescriptorFilter""")
               appendLine("""import io.kotest.engine.TestEngineLauncher""")
               appendLine("""import io.kotest.core.spec.SpecRef""")
               appendLine("""import kotlin.test.Test""")
               appendLine("""import kotlin.test.AfterClass""")
               appendLine("""import kotlinx.cinterop.ExperimentalForeignApi""")
               appendLine("""import kotlinx.cinterop.toKString""")
               appendLine("""import platform.posix.getenv""")

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
@OptIn(ExperimentalForeignApi::class)
@AfterClass
fun runKotest() {

  val descriptorArg = getenv("kotest.framework.runtime.native.descriptor")?.toKString()
  val listener = getenv("kotest.framework.runtime.native.listener")?.toKString()

  val descriptor = descriptorArg?.let { DescriptorPaths.parse(it) }
  val filter = descriptor?.let { ProvidedDescriptorFilter(descriptor) }

  val launcher = TestEngineLauncher()
   .withNative()
   .addExtensions(listOfNotNull(filter))
   .withSpecRefs("""
               )

               specs.forEach {
                  appendLine("""SpecRef.Function({ $it() }, $it::class),""")
               }

               appendLine(
                  """   )
   when (listener) {
      "TeamCity" -> launcher.withTeamCityListener().launch()
      else -> launcher.launch()
   }
}"""
               )
            }
         )
      }
   }
}
