package io.kotest.framework.symbol.processor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.symbol.Modifier

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
      if (isPublic(classDeclaration) && isSpec(classDeclaration)) {
         specs.add(classDeclaration)
      } else if (isConfig(classDeclaration)) {
         configs.add(classDeclaration)
      }
   }

   internal fun isPublic(declaration: KSClassDeclaration): Boolean =
      !declaration.modifiers.contains(Modifier.PRIVATE)

   internal fun isSpec(declaration: KSClassDeclaration): Boolean =
      declaration.getAllSuperTypes().map { it.declaration }
         .filterIsInstance<KSClassDeclaration>()
         .any { specTypes.contains(it.simpleName.asString()) }

   internal fun isConfig(declaration: KSClassDeclaration): Boolean {
      return declaration.getAllSuperTypes().map { it.declaration }
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
      validateProjectConfigs()
      val files = visitor.specs.mapNotNull { it.containingFile }
      TestEngineGenerator(environment).generate(files, visitor.specs, visitor.configs)
   }

   private fun validateProjectConfigs() {
      if (visitor.configs.size > 1) {
         val configs = visitor.configs.joinToString(";") { it.qualifiedName?.asString() ?: it.simpleName.asString() }
         error("Only one ProjectConfig is allowed, found $configs")
      }
   }
}

@Suppress("unused") // is referenced in the service resource file
class KotestSymbolProcessorProvider : SymbolProcessorProvider {
   override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
      return KotestSymbolProcessor(environment)
   }
}
