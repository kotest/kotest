package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.JsPlatformInfo
import com.google.devtools.ksp.processing.NativePlatformInfo
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ARRAY
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.UNIT

class TestEngineGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(
      files: List<KSFile>,
      specs: List<KSClassDeclaration>,
      configs: List<KSClassDeclaration>
   ) {

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

   // on native we use https://youtrack.jetbrains.com/issue/KT-63218/EagerInitialization-use-cases
   private fun createNativeEntryPoint(): PropertySpec {
      val prop = PropertySpec.builder("testEngineEntryPoint", UNIT)
         .addAnnotation(ClassName("kotlin.native", "EagerInitialization"))
         .addAnnotation(
            AnnotationSpec.builder(ClassName("kotlin", "OptIn")).addMember("ExperimentalStdlibApi::class").build()
         )
         .initializer("""runBlocking { launch() }""".trim())
      return prop.build()
   }

   // on JS and WasmJS it is enough to create a main method, and the kotlin compiler will find and invoke it
   private fun createJsEntryPoint(): FunSpec {
      val function = FunSpec.builder("main")
         .addParameter(ParameterSpec.builder("args", ARRAY.parameterizedBy(STRING)).build())
         .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)
         .addCode("""launch()""".trim())
      return function.build()
   }

   private fun createLauncherFunction(
      specs: List<KSClassDeclaration>,
      configs: List<KSClassDeclaration>
   ): FunSpec {

      if (specs.isEmpty()) {
         return FunSpec.builder("launch")
            .addModifiers(KModifier.PUBLIC)
            .build()
      }

      val function = FunSpec.builder("launch")
         .addModifiers(KModifier.PUBLIC, KModifier.SUSPEND)
         .addAnnotation(
            AnnotationSpec.builder(ClassName("kotlin", "Suppress")).addMember("\"RemoveRedundantBackticks\"").build()
         )
         .addCode("\n")
         .addCode(
            """
val config = ${if (configs.isEmpty()) "null" else (configs.first().qualifiedName?.asString() + "()")}
val specs = listOf(
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
      function.addCode(
         """invokeTestEngine(specs, config)""".trim()
      ).addCode("\n")

      return function.build()
   }

   internal fun createFileSpec(
      specs: List<KSClassDeclaration>,
      configs: List<KSClassDeclaration>
   ): FileSpec {

      val file = FileSpec.builder("io.kotest.framework.runtime", "kotest.kt")
         .addImport("io.kotest.core.spec", "SpecRef")
         .addImport("io.kotest.engine.launcher", "invokeTestEngine")

      when (environment.platforms.first()) {
         is NativePlatformInfo ->
            // native uses runBlocking to launch the engine
            file.addImport("kotlinx.coroutines", "runBlocking")
      }

      file.addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "Suppress")).addMember("\"DEPRECATION\"").build())

      specs.forEachIndexed { index, spec ->
         file.addAliasedImport(
            ClassName(spec.packageName.asString(), spec.simpleName.asString()),
            `as` = spec.simpleName.asString() + index.toString()
         )
      }

      file.addFunction(createLauncherFunction(specs, configs))

      when (val platform = environment.platforms.first()) {
         is NativePlatformInfo -> file.addProperty(createNativeEntryPoint())
         is JsPlatformInfo -> file.addFunction(createJsEntryPoint())
         else if platform.platformName.lowercase().contains("wasm") -> file.addFunction(createJsEntryPoint())
      }

      return file.build()
   }
}
