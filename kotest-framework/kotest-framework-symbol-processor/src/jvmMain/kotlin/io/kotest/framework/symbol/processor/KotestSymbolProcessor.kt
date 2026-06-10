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

   internal val specs = mutableListOf<KSClassDeclaration>()
   internal val configs = mutableListOf<KSClassDeclaration>()

   override fun visitFile(file: KSFile, data: Unit) {
      file.declarations.forEach { it.accept(this, Unit) }
   }

   override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
      super.visitClassDeclaration(classDeclaration, data)
      if (isPublic(classDeclaration) && !isAbstract(classDeclaration) && isSpec(classDeclaration)) {
         addIfAbsent(specs, classDeclaration)
      } else if (isConfig(classDeclaration)) {
         addIfAbsent(configs, classDeclaration)
      }
   }

   /**
    * Adds [declaration] to [declarations] unless a declaration with the same fully-qualified
    * name has already been collected. KSP invokes [io.kotest.framework.symbol.processor.KotestSymbolProcessor.process]
    * once per processing round, so without deduplication a class visited in more than one round
    * would be registered multiple times in the generated entry point.
    */
   private fun addIfAbsent(declarations: MutableList<KSClassDeclaration>, declaration: KSClassDeclaration) {
      val name = declaration.qualifiedName?.asString() ?: declaration.simpleName.asString()
      if (declarations.none { (it.qualifiedName?.asString() ?: it.simpleName.asString()) == name }) {
         declarations.add(declaration)
      }
   }

   internal fun isPublic(declaration: KSClassDeclaration): Boolean =
      !declaration.modifiers.contains(Modifier.PRIVATE)

   /**
    * Returns true for classes that cannot be instantiated directly (abstract or sealed).
    * The generated entry point invokes the spec's no-arg constructor (e.g. `MySpec()`),
    * so abstract / sealed spec subclasses must be excluded — including them produces
    * generated code that fails to compile.
    */
   internal fun isAbstract(declaration: KSClassDeclaration): Boolean =
      declaration.modifiers.contains(Modifier.ABSTRACT) || declaration.modifiers.contains(Modifier.SEALED)

   /**
    * Returns true for subclasses of [io.kotest.core.spec.Spec]. Every spec style
    * (FunSpec, StringSpec, AnnotationSpec, ...) ultimately extends Spec, so this
    * detects them all — including user-defined intermediate base specs — without
    * having to enumerate the built-in style names.
    */
   internal fun isSpec(declaration: KSClassDeclaration): Boolean =
      declaration.getAllSuperTypes().map { it.declaration }
         .filterIsInstance<KSClassDeclaration>()
         .any { it.qualifiedName?.asString() == "io.kotest.core.spec.Spec" }

   internal fun isConfig(declaration: KSClassDeclaration): Boolean {
      return declaration.getAllSuperTypes().map { it.declaration }
         .filterIsInstance<KSClassDeclaration>()
         .any { it.qualifiedName?.asString() == "io.kotest.core.config.AbstractProjectConfig" }
   }
}

class KotestSymbolProcessor(private val environment: SymbolProcessorEnvironment) : SymbolProcessor {

   val visitor = KotestFileVisitor()

   override fun process(resolver: Resolver): List<KSAnnotated> {
      // process is invoked once per KSP round, so only visit files new to this round,
      // otherwise specs would be collected (and thus registered) once per round
      resolver.getNewFiles().forEach { it.accept(visitor, Unit) }
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
