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

class FindSpecsVisitor : KSVisitorVoid() {

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

   private fun hasSpecSupertype(supertypes: Collection<KSType>): Boolean {
      return supertypes.map { it.declaration }
         .filterIsInstance<KSClassDeclaration>()
         .any { specTypes.contains(it.simpleName.asString()) }
   }

   private fun isSpec(classDeclaration: KSClassDeclaration): Boolean {
      val supers = classDeclaration.getAllSuperTypes().toList()
      return hasSpecSupertype(supers)
   }

   override fun visitFile(file: KSFile, data: Unit) {
      file.declarations.forEach { it.accept(this, Unit) }
   }

   override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      super.visitClassDeclaration(classDeclaration, data)
      if (isSpec(classDeclaration)) {
         specs.add(classDeclaration)
      }
   }
}

class KotestSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

   val visitor = FindSpecsVisitor()

   override fun process(resolver: Resolver): List<KSAnnotated> {
      resolver.getAllFiles().forEach { it.accept(visitor, Unit) }
      return emptyList()
   }

   override fun finish() {
      val files = visitor.specs.mapNotNull { it.containingFile }
      when (environment.platforms.first()) {
         is JsPlatformInfo -> JSGenerator(environment).generate(files, visitor.specs)
         is NativePlatformInfo -> NativeGenerator(environment).generate(files, visitor.specs)
         else -> Unit
      }
   }
}

class KotestSymbolProcessorProvider : SymbolProcessorProvider {
   override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
      return KotestSymbolProcessor(environment)
   }
}
