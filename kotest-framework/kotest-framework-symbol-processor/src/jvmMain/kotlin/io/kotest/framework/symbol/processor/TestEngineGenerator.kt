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
import kotlin.js.ExperimentalJsExport

class TestEngineGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>, configs: List<KSClassDeclaration>) {

      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime", // the package for the generated entry point
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
         .addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "OptIn")).addMember("KotestInternal::class").build())
         .addCode("\n")
         .addCode(
            """
val launcher = TestEngineLauncher()
 .withSpecRefs(
    """.trim()
         ).addCode("\n")
      specs.forEachIndexed { index, spec ->
         val sn = spec.simpleName.asString() + index
         val fqn = spec.qualifiedName?.asString() ?: spec.simpleName.asString()
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
         """invokeTestEngineLauncher(launcher)""".trim()
      ).addCode("\n")

      val file = FileSpec.builder("io.kotest.framework.runtime", "kotest.kt")
         .addFunction(function.build())
         .addImport("io.kotest.common", "KotestInternal")
         .addImport("io.kotest.core.spec", "SpecRef")
         .addImport("io.kotest.engine", "TestEngineLauncher")
      specs.forEachIndexed { index, spec ->
         file.addAliasedImport(
            ClassName(spec.packageName.asString(), spec.simpleName.asString()),
            `as` = spec.simpleName.asString() + index.toString()
         )
      }
      return file.build()
   }
}
