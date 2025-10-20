package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.JsPlatformInfo
import com.google.devtools.ksp.processing.NativePlatformInfo
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSVisitorVoid

class KotestFileVisitor : KSVisitorVoid() {

   private val specTypes = setOf(
      "AnnotationSpec",
      "BehaviorSpec",
      "DescribeSpec",
      "ExpectSpec",
      "FeatureSpec",
      "FreeSpec",
      "FunSpec",
      "ShouldSpec",
      "StringSpec",
      "WordSpec",
   )

   internal val specs = mutableListOf<KSClassDeclaration>()
   internal val configs = mutableListOf<KSClassDeclaration>()

   override fun visitFile(file: KSFile, data: Unit) {
      file.declarations.forEach { it.accept(this, Unit) }
   }

   override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      super.visitClassDeclaration(classDeclaration, data)
      val supers = classDeclaration.getAllSuperTypes().toList()
      if (hasSpecSupertype(supers)) {
         specs.add(classDeclaration)
      } else if (hasConfigSupertype(supers)) {
         configs.add(classDeclaration)
      }
   }

   private fun hasSpecSupertype(supertypes: Collection<KSType>): Boolean {
      return supertypes.map { it.declaration }
         .filterIsInstance<KSClassDeclaration>()
         .any { specTypes.contains(it.simpleName.asString()) }
   }

   private fun hasConfigSupertype(supertypes: Collection<KSType>): Boolean {
      return supertypes.map { it.declaration }
         .filterIsInstance<KSClassDeclaration>()
         .any { it.qualifiedName?.asString() == "io.kotest.core.config.AbstractProjectConfig" }
   }
}

class KotestSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

   val visitor = KotestFileVisitor()

   override fun process(resolver: Resolver): List<KSAnnotated> {
      resolver.getAllFiles().forEach { it.accept(visitor, Unit) }
      return emptyList()
   }

   override fun finish() {
      if (visitor.configs.size > 1)
         error("Only one ProjectConfig is allowed, found ${visitor.configs.size}:\n${visitor.configs.joinToString("\n") { it.qualifiedName?.asString() ?: it.simpleName.asString() }}")
      val files = visitor.specs.mapNotNull { it.containingFile }
      when (val platform = environment.platforms.first()) {
         is JsPlatformInfo -> JSGenerator(environment).generate(files, visitor.specs, visitor.configs)
         is NativePlatformInfo -> NativeGenerator(environment).generate(files, visitor.specs, visitor.configs)
         // todo we need some way of determining if the Wasi variant of Wasm, so we can use the WasmWasiGenerator
         // see https://github.com/google/ksp/issues/2544
         else if platform.platformName.contains("wasm-js") ->
            WasmJsGenerator(environment).generate(files, visitor.specs, visitor.configs)
         else -> error("Unsupported platform: ${environment.platforms.first()}")
      }
   }
}

@Suppress("unused") // is referenced in the service resource file
class KotestSymbolProcessorProvider : SymbolProcessorProvider {
   override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
      return KotestSymbolProcessor(environment)
   }
}
