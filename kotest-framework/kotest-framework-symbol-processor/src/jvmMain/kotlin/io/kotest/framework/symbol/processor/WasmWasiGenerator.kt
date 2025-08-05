package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import kotlin.js.ExperimentalJsExport

class WasmWasiGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>, configs: List<KSClassDeclaration>) {

      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.wasi",
         fileName = "kotest",
         extensionName = "kt"
      )

      outputStream.bufferedWriter().use { writer ->
         writer.write(createFileSpec(specs, configs).toString())
      }
   }

   private fun createFileSpec(specs: List<KSClassDeclaration>, configs: List<KSClassDeclaration>): FileSpec {
      val function = FunSpec.builder("runKotest")
         .addModifiers(KModifier.PUBLIC)
         .addAnnotation(ExperimentalJsExport::class)
         .addAnnotation(ClassName("kotlin.wasm", "WasmExport"))
         .addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "OptIn")).addMember("KotestInternal::class").build())
         .addParameter(ParameterSpec.builder("listenerType", Int::class).build())
         .addCode("\n")
         .addCode(
            """
val launcher = TestEngineLauncher()
 .withWasmWasi()
 .withSpecRefs(
    """.trim()
         ).addCode("\n")
      specs.forEach {
         val sn = it.simpleName.asString()
         val fqn = it.qualifiedName?.asString() ?: it.simpleName.asString()
         function.addCode("""SpecRef.Function ({ `${sn}`() }, `${sn}`::class, "$fqn"), """)
         function.addCode("\n")
      }
      function
         .addCode(""")""")
         .addCode("\n")
      if (configs.isNotEmpty()) {
         function
            .addCode(""".withProjectConfig(${configs.first().qualifiedName?.asString()}())""")
            .addCode("\n")
      }
      function.addCode(
         """
when (listenerType) {
   1 -> GlobalScope.launch { launcher.withTeamCityListener().async() }
   else -> GlobalScope.launch { launcher.withConsoleListener().async() }
}
""".trim()
      ).addCode("\n")

      val file = FileSpec.builder("io.kotest.framework.runtime.wasi", "kotest.kt")
         .addFunction(function.build())
         .addImport("kotlinx.coroutines", "GlobalScope")
         .addImport("kotlinx.coroutines", "launch")
         .addImport("kotlinx.coroutines", "delay")
         .addImport("io.kotest.common", "KotestInternal")
         .addImport("io.kotest.core.spec", "SpecRef")
         .addImport("io.kotest.engine", "TestEngineLauncher")
      specs.forEach {
         file.addImport(it.packageName.asString(), it.simpleName.asString())
      }
      return file.build()
   }
}
