package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asTypeName
import kotlin.js.ExperimentalJsExport

class JSGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>, configs: List<KSClassDeclaration>) {

      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.js",
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
         .addAnnotation(ClassName("kotlin.js", "JsExport"))
         .addParameter(ParameterSpec.builder("listenerType", String::class).build())
         .addParameter(ParameterSpec.builder("descriptorArg", String::class.asTypeName().copy(nullable = true)).build())
         .addCode(
            """
val descriptor = descriptorArg?.let { DescriptorPaths.parse(it) }
val filter = descriptor?.let { ProvidedDescriptorFilter(descriptor) }
""".trim()
         )
         .addCode("\n")
         .addCode(
            """
val launcher = TestEngineLauncher()
 .withJs()
 .addExtensions(listOfNotNull(filter))
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
   "teamcity" -> launcher.withTeamCityListener().promise()
   else -> launcher.promise()
}
""".trim()
      ).addCode("\n")

      val file = FileSpec.builder("io.kotest.framework.runtime.js", "kotest.kt")
         .addFunction(function.build())
         .addImport("io.kotest.core.descriptors", "DescriptorPaths")
         .addImport("io.kotest.engine.extensions", "ProvidedDescriptorFilter")
         .addImport("io.kotest.engine", "TestEngineLauncher")
         .addImport("io.kotest.core.spec", "SpecRef")
      specs.forEach {
         file.addImport(it.packageName.asString(), it.simpleName.asString())
      }
      return file.build()
   }
}
