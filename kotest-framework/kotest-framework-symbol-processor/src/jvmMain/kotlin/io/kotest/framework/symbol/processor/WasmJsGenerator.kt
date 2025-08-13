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
import com.squareup.kotlinpoet.UNIT

class WasmJsGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>, configs: List<KSClassDeclaration>) {

      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.wasmjs",
         fileName = "kotest",
         extensionName = "kt"
      )

      outputStream.bufferedWriter().use { writer ->
         writer.write(createFileSpec(specs, configs).toString())
      }
   }

   private fun createFileSpec(specs: List<KSClassDeclaration>, configs: List<KSClassDeclaration>): FileSpec {
      val function = FunSpec.builder("main")
         .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)
         .returns(UNIT)
         .addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "OptIn")).addMember("KotestInternal::class").build())
         .addCode(
            """
//val descriptor = includeArg?.let { DescriptorPaths.parse(it) }
//val filter = descriptor?.let { IncludeDescriptorFilter(it) }
""".trim()
         )
         .addCode("\n")
         .addCode(
            """
val promise = TestEngineLauncher()
 .withWasmJs()
// .addExtensions(listOfNotNull(filter))
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
      function
         .addCode(""".withConsoleListener()""")
         .addCode("\n")
         .addCode(""".promise() as Promise<JsAny?>""")
         .addCode("\n")

// fail the execution if there are any test failures or errors
      function.addCode(
         """
val result = promise.await<EngineResult>()
//if (result.errors.isNotEmpty() || result.testFailures) {
//   error("Tests failed")
//}
"""
      )

      val file = FileSpec.builder("io.kotest.framework.runtime.wasmjs", "kotest.kt")
         .addFunction(function.build())
         .addImport("io.kotest.common", "KotestInternal")
         .addImport("io.kotest.core.descriptors", "DescriptorPaths")
         .addImport("io.kotest.core.spec", "SpecRef")
         .addImport("io.kotest.engine", "EngineResult")
         .addImport("io.kotest.engine", "TestEngineLauncher")
         .addImport("io.kotest.engine.extensions", "IncludeDescriptorFilter")
         .addImport("kotlinx.coroutines", "await")
         .addImport("kotlin.js", "Promise")
      specs.forEach {
         file.addImport(it.packageName.asString(), it.simpleName.asString())
      }
      return file.build()
   }
}
