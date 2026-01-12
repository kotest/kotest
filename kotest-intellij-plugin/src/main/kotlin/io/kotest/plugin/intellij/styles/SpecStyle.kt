package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.TestName
import io.kotest.plugin.intellij.TestType
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.hasFunctionName
import io.kotest.plugin.intellij.psi.isContainedInSpecificSpec
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtConstructorCalleeExpression
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtNameReferenceExpression
import org.jetbrains.kotlin.psi.KtOperationReferenceExpression
import org.jetbrains.kotlin.psi.KtPackageDirective
import org.jetbrains.kotlin.psi.KtParameterList
import org.jetbrains.kotlin.psi.KtStringTemplateExpression
import org.jetbrains.kotlin.psi.KtTypeArgumentList
import org.jetbrains.kotlin.psi.KtTypeParameterList
import org.jetbrains.kotlin.psi.KtTypeReference

interface SpecStyle {

   companion object {

      // for speed when detecting via psi elements, we order this in the types most common
      val styles = listOf(
         FunSpecStyle,
         BehaviorSpecStyle,
         DescribeSpecStyle,
         FreeSpecStyle,
         StringSpecStyle,
         ExpectSpecStyle,
         FeatureSpecStyle,
         ShouldSpecStyle,
         WordSpecStyle,
         AnnotationSpecStyle,
      )

      /**
       * For the given [PsiElement], if it is contained within a Kotest test definition, then
       * it will return a [Test] instance that models that test.
       */
      fun findTest(element: PsiElement): Test? {
         return styles.asSequence()
            .filter { it.isContainedInSpec(element) }
            .mapNotNull { it.findAssociatedTest(element) }
            .firstOrNull()
      }

      // in future iterations this could change and be somehow saying running all data tests within the spec
      val dataTestDefaultTestName: TestName = TestName(null, "All Spec Tests, including data tests", interpolated = false)
   }

   /**
    * Returns true if the given [PsiElement] is located within a class which extends
    * the spec class associated with this style instance.
    */
   fun isContainedInSpec(element: PsiElement): Boolean = element.isContainedInSpecificSpec(fqn())

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
      return generateSequence(element) { it.parent }.mapNotNull { test(it) }.firstOrNull()
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
    * Returns true if this element could be the canoncial leaf element for a test in this spec style.
    */
   fun isMaybeCanoncialTestLeafElement(element: LeafPsiElement): Boolean = false

   /**
    * Returns a set of the leaf element types that could be the canoncial leaf of a test
    */
   fun possibleLeafElements(): Set<String>

   /**
    * Returns all tests located in the given [PsiElement] as a tree of elements.
    * Nested tests are associated with their parent TestElement.
    *
    * @root if set to true will only include top level tests
    */
   fun tests(element: PsiElement, root: Boolean): List<TestElement> {
      return element.children.flatMap { child ->
         when (child) {
            // there are some element types we don't need to traverse to save time and nested traversals
            is KtImportList, is KtPackageDirective -> emptyList()
            is KtConstructorCalleeExpression -> emptyList()
            is KtStringTemplateExpression -> emptyList()
            is KtTypeArgumentList, is KtTypeParameterList -> emptyList()
            is KtOperationReferenceExpression -> emptyList()
            is KtParameterList, is KtDeclarationModifierList, is KtTypeReference, is KtNameReferenceExpression -> emptyList()
            else ->
               when (val test = test(child)) {
                  null -> tests(child, root)
                  else ->
                     // if root is true, we don't need to go any further than the first test we find
                     when (root) {
                        true -> listOf(TestElement(child, test, emptyList()))
                        else ->
                           // if the test is a TestType.Test we don't need to inspect the children
                           // because we know there can't be any further tests nested
                           when (test.testType) {
                              TestType.Container -> listOf(TestElement(child, test, tests(child, false)))
                              TestType.Test -> listOf(TestElement(child, test, emptyList()))
                           }
                     }
               }
         }
      }
   }

   /**
    * Returns the fully qualified name of the base spec type, eg io.kotest.core.specs.style.FunSpec.
    */
   fun fqn(): FqName

   /**
    * Returns a test for a method with the given name, in a way that is compatible with this style.
    * For example, a [FunSpec] would return a string like this: test("given name") { }
    */
   fun generateTest(specName: String, name: String): String

   // TODO default will be removed if this POC is accepted and all other styles implement it
   fun getDataTestMethodNames() : Set<String> = emptySet()

   /**
    * A test container of the form:
    *```
    *   withXXX(1, 2, 3) { }
    *   withXXX(listOf(1, 2, 3)) { }
    *   withXXX(nameFn = { "test $it" }, 1, 2, 3) { }
    *   ...
    *```
    * plus any other withXXX permutation as per result of [getDataTestMethodNames].
    *
    * Note: even tho we build a [Test], with some params, the runner will only read the [Test.isDataTest] boolean and determine it needs to run the whole spec.
    */
   fun KtCallExpression.tryDataTest(): Test? {
      val specClass = enclosingKtClassOrObject() ?: return null

      if (!hasFunctionName(getDataTestMethodNames().toList())) return null

      return Test(dataTestDefaultTestName, null, specClass, TestType.Container, xdisabled = false, psi = this, isDataTest = true)
   }
}
