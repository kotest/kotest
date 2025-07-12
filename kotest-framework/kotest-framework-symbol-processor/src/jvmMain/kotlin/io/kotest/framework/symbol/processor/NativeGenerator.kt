package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.UNIT

class NativeGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>) {
      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.native",
         fileName = "kotest",
         extensionName = "kt"
      )
      outputStream.bufferedWriter().use { writer ->
         writer.write(createFileSpec(specs).toString())
      }
   }

   // https://youtrack.jetbrains.com/issue/KT-63218/EagerInitialization-use-cases

   private fun createFileSpec(specs: List<KSClassDeclaration>): FileSpec {
      val function = FunSpec.builder("runKotest")
         .addModifiers(KModifier.PUBLIC)
         .addAnnotation(ClassName("kotlinx.cinterop", "ExperimentalForeignApi"))
         .addCode(
            """
val descriptorArg = getenv("kotest.framework.runtime.native.descriptor")?.toKString()
val listenerType = getenv("kotest.framework.runtime.native.listener")?.toKString()

val descriptor = descriptorArg?.let { DescriptorPaths.parse(it) }
val filter = descriptor?.let { ProvidedDescriptorFilter(descriptor) }
""".trim()
         )
         .addCode("\n")
         .addCode(
            """
val launcher = TestEngineLauncher()
 .withNative()
 .addExtensions(listOfNotNull(filter))
 .withSpecRefs(
    """.trim()
         ).addCode("\n")
      specs.forEach {
         function.addCode("""SpecRef.Function ({ `$it`() }, `$it`::class), """)
         function.addCode("\n")
      }
      function
         .addCode(""")""")
         .addCode("\n")
         .addCode(
            """
when (listenerType) {
   "teamcity" -> launcher.withTeamCityListener().launch()
   else -> launcher.launch()
}
""".trim()
         ).addCode("\n")

      val invoker = PropertySpec.builder("invoker", UNIT)
         .addAnnotation(ClassName("kotlin.native", "EagerInitialization"))
         .addAnnotation(ClassName("kotlin", "ExperimentalStdlibApi"))
         .addAnnotation(ClassName("kotlinx.cinterop", "ExperimentalForeignApi"))
         .initializer("""runKotest()""")

      val file = FileSpec.builder("io.kotest.framework.runtime.native", "kotest.kt")
         .addFunction(function.build())
         .addProperty(invoker.build())
         .addImport("io.kotest.core.descriptors", "DescriptorPaths")
         .addImport("io.kotest.engine.extensions", "ProvidedDescriptorFilter")
         .addImport("io.kotest.engine", "TestEngineLauncher")
         .addImport("io.kotest.core.spec", "SpecRef")
         .addImport("kotlinx.cinterop", "toKString")
         .addImport("platform.posix", "getenv")
      specs.forEach {
         file.addImport(it.qualifiedName!!.asString().substringBeforeLast("."), it. simpleName.asString())
      }
      return file.build()
   }
}
