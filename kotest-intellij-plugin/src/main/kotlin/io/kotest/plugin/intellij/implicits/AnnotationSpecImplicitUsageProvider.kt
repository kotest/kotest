package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.isContainedInSpecificSpec
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.analysis.api.types.symbol
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClass
import org.jetbrains.kotlin.psi.KtNamedFunction

/**
 * Allows disabling highlighting of certain elements as unused when such elements are not referenced
 * from the code but are referenced in some other way.
 *
 * This [ImplicitUsageProvider] marks methods annotated with AnnotationSpec lifecycle annotations
 * (such as [io.kotest.core.spec.style.AnnotationSpec.BeforeEach],
 * [io.kotest.core.spec.style.AnnotationSpec.AfterEach], etc.) as used, so that IntelliJ does not
 * highlight them as unused declarations.
 *
 * It also marks inner classes annotated with [io.kotest.core.spec.style.AnnotationSpec.Nested]
 * inside an AnnotationSpec subclass as used.
 */
class AnnotationSpecImplicitUsageProvider : ImplicitUsageProvider {

   private val annotationSpecFqn = FqName("io.kotest.core.spec.style.AnnotationSpec")

   private val lifecycleAnnotationFqns = setOf(
      FqName("io.kotest.core.spec.style.AnnotationSpec.Test"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.BeforeEach"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.Before"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.BeforeAll"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.BeforeClass"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.AfterEach"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.After"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.AfterAll"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.AfterClass"),
      FqName("io.kotest.core.spec.style.AnnotationSpec.Ignore"),
   )

   private val nestedAnnotationFqn = FqName("io.kotest.core.spec.style.AnnotationSpec.Nested")

   // Short names for quick pre-filtering before expensive FQN resolution
   private val lifecycleAnnotationShortNames = lifecycleAnnotationFqns.map { it.shortName().asString() }.toSet()

   override fun isImplicitWrite(element: PsiElement): Boolean = false
   override fun isImplicitRead(element: PsiElement): Boolean = false

   override fun isImplicitUsage(element: PsiElement): Boolean {
      return when (element) {
         is KtNamedFunction -> isAnnotationSpecFunction(element)
         is KtClass -> isNestedAnnotationSpecClass(element)
         else -> false
      }
   }

   private fun isAnnotationSpecFunction(function: KtNamedFunction): Boolean {
      val candidates = function.annotationEntries.filter {
         it.shortName?.asString() in lifecycleAnnotationShortNames
      }
      if (candidates.isEmpty()) return false
      if (candidates.none { isAnnotationWithFqn(it, lifecycleAnnotationFqns) }) return false
      return function.isContainedInSpecificSpec(annotationSpecFqn)
   }

   private fun isNestedAnnotationSpecClass(ktClass: KtClass): Boolean {
      val candidates = ktClass.annotationEntries.filter { it.shortName?.asString() == "Nested" }
      if (candidates.isEmpty()) return false
      if (candidates.none { isAnnotationWithFqn(it, setOf(nestedAnnotationFqn)) }) return false
      return ktClass.isContainedInSpecificSpec(annotationSpecFqn)
   }

   private fun isAnnotationWithFqn(entry: KtAnnotationEntry, fqns: Set<FqName>): Boolean {
      val typeRef = entry.typeReference ?: return false
      return try {
         analyze(entry) {
            val fqn = typeRef.type.symbol?.classId?.asSingleFqName() ?: return@analyze false
            fqn in fqns
         }
      } catch (_: Exception) {
         false
      }
   }
}
