package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.isContainedInSpec
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtSuperTypeList
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtTypeReference

interface SpecStyle {

   companion object {
      val styles = listOf(
         BehaviorSpecStyle,
         DescribeSpecStyle,
         ExpectSpecStyle,
         FeatureSpecStyle,
         FreeSpecStyle,
         FunSpecStyle,
         ShouldSpecStyle,
         StringSpecStyle,
         WordSpecStyle
      )
   }

   /**
    * Returns true if the given [PsiElement] is located within a class which extends
    * the spec class associated with this style instance.
    */
   fun isContainedInSpec(element: PsiElement): Boolean = element.isContainedInSpec(fqn())

   /**
    * Returns a [Test] if this [PsiElement] is the canonical root of a test AST.
    */
   fun test(element: PsiElement): Test? = null

   /**
    * If we know that a particular [PsiElement] is located within a test definition, then we can
    * use this method to find the associated [Test] instance.
    *
    * This method should only be called when we have a single element and want to find the test
    * associated with it. It should not be called when iterating over all elements looking
    * speculatively for tests.
    */
   fun findAssociatedTest(element: PsiElement): Test? {
      return generateSequence(element, { it.parent }).mapNotNull { test(it) }.firstOrNull()
   }

   /**
    * Returns a [Test] if this [LeafPsiElement] is the canonical leaf of a test AST.
    * This method will first try to determine if this leaf element is inside a test, and then
    * will invoke [test] with the container [PsiElement].
    */
   fun test(element: LeafPsiElement): Test? = null

   fun specStyleName(): String

   fun isTestElement(element: PsiElement): Boolean = test(element) != null

   /**
    * Returns all child tests located in the given [PsiElement].
    */
   fun tests(element: PsiElement): List<TestElement> {
      return element.children.flatMap { child ->
         when (child) {
            // there are some element types we don't need to traverse to cycles and nested traversals
            is KtParameterList, is KtSuperTypeList, is KtPackageDirective, is KtTypeArgumentList, is KtImportList, is KtTypeParameterList, is KtDeclarationModifierList, is KtTypeReference, is KtNameReferenceExpression -> emptyList()
            else ->
               when (val test = test(child)) {
                  null -> tests(child)
                  else ->
                     // if the test is a Container we don't need to inspect the children
                     when (test.testType) {
                        TestType.Container -> listOf(TestElement(child, test, tests(child)))
                        TestType.Test -> listOf(TestElement(child, test, emptyList()))
                     }
               }
         }
      }
   }

   /**
    * Returns the fully qualified name of the spec parent class, eg io.kotest.core.specs.style.FunSpec.
    */
   fun fqn(): FqName

   /**
    * Returns a test for a method with the given name, in a way that is compatible with this style.
    * For example, a [FunSpec] would return a string like this: test("given name") { }
    */
   fun generateTest(specName: String, name: String): String
}
