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
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.UNIT

class NativeGenerator(private val environment: SymbolProcessorEnvironment) {

   fun generate(files: List<KSFile>, specs: List<KSClassDeclaration>, configs: MutableList<KSClassDeclaration>) {
      val outputStream = environment.codeGenerator.createNewFile(
         dependencies = Dependencies(true, *files.toTypedArray()),
         packageName = "io.kotest.framework.runtime.native",
         fileName = "kotest",
         extensionName = "kt"
      )
      outputStream.bufferedWriter().use { writer ->
         writer.write(createFileSpec(specs, configs).toString())
      }
   }

   // https://youtrack.jetbrains.com/issue/KT-63218/EagerInitialization-use-cases

   private fun createFileSpec(specs: List<KSClassDeclaration>, configs: MutableList<KSClassDeclaration>): FileSpec {
      val function = FunSpec.builder("runKotest")
         .addModifiers(KModifier.PUBLIC)
         .addAnnotation(ClassName("kotlinx.cinterop", "ExperimentalForeignApi"))
         .addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "OptIn")).addMember("KotestInternal::class").build())
         .addCode(
            """
val includeArg = getenv("kotest.framework.runtime.native.include")?.toKString()
val listenerType = getenv("kotest.framework.runtime.native.listener")?.toKString() ?: ""
val moduleTestReportsDir = getenv("kotest.framework.runtime.native.module.test.reports.dir")?.toKString()
val rootTestReportsDir = getenv("kotest.framework.runtime.native.root.test.reports.dir")?.toKString()

val descriptor = includeArg?.let { DescriptorPaths.parse(it) }
val filter = descriptor?.let { IncludeDescriptorFilter(descriptor) }
val moduleXmlReporter = moduleTestReportsDir?.let { JunitXmlReportTestEngineListener(it, null, null) }
val rootXmlReporter = rootTestReportsDir?.let { JunitXmlReportTestEngineListener(it, null, null) }

""".trim()
         )
         .addCode("\n")
         .addCode(
            """
val launcher = TestEngineLauncher()
 .withNative()
 .addExtensions(listOfNotNull(filter))
 .withListener(moduleXmlReporter)
 .withListener(rootXmlReporter)
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
   "teamcity" -> launcher.withTeamCityListener().launch()
   "console" -> launcher.withConsoleListener().launch()
   else -> Unit // this stops us running from the non-kotest test targets
}
""".trim()
      ).addCode("\n")

      val invoker = PropertySpec.builder("invoker", UNIT)
         .addAnnotation(ClassName("kotlin.native", "EagerInitialization"))
         .addAnnotation(ClassName("kotlin", "ExperimentalStdlibApi"))
         .addAnnotation(ClassName("kotlinx.cinterop", "ExperimentalForeignApi"))
         .initializer("""runKotest()""")

      val file = FileSpec.builder("io.kotest.framework.runtime.native", "kotest.kt")
         .addAnnotation(AnnotationSpec.builder(ClassName("kotlin", "Suppress")).addMember("\"DEPRECATION\"").build())
         .addFunction(function.build())
         .addProperty(invoker.build())
         .addImport("kotlinx.cinterop", "toKString")
         .addImport("platform.posix", "getenv")
         .addImport("io.kotest.common", "KotestInternal")
         .addImport("io.kotest.core.descriptors", "DescriptorPaths")
         .addImport("io.kotest.core.spec", "SpecRef")
         .addImport("io.kotest.engine", "TestEngineLauncher")
         .addImport("io.kotest.engine.extensions", "IncludeDescriptorFilter")
         .addImport("io.kotest.engine.reports", "JunitXmlReportTestEngineListener")
      specs.forEach {
         file.addImport(it.packageName.asString(), it.simpleName.asString())
      }
      return file.build()
   }
}
