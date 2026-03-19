package io.kotest.plugin.intellij.implicits

import com.intellij.codeInsight.daemon.ImplicitUsageProvider
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.psi.isContainedInSpecificSpec
import org.jetbrains.kotlin.name.FqName
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

   private val lifecycleAnnotationNames = setOf(
      "Test",
      "BeforeEach", "Before",
      "BeforeAll", "BeforeClass",
      "AfterEach", "After",
      "AfterAll", "AfterClass",
      "Ignore",
   )

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
      val annotationNames = function.annotationEntries.mapNotNull { it.shortName?.asString() }
      if (annotationNames.none { it in lifecycleAnnotationNames }) return false
      return function.isContainedInSpecificSpec(annotationSpecFqn)
   }

   private fun isNestedAnnotationSpecClass(ktClass: KtClass): Boolean {
      val hasNested = ktClass.annotationEntries.any { it.shortName?.asString() == "Nested" }
      if (!hasNested) return false
      return ktClass.isContainedInSpecificSpec(annotationSpecFqn)
   }
}
