package io.kotest.plugin.intellij.styles

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.styles.psi.isContainedInSpec
import org.jetbrains.kotlin.name.FqName

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

   fun PsiElement.isContainedInSpec(): Boolean = this.isContainedInSpec(fqn())

   /**
    * Returns a [Test] if this [PsiElement] is the container of a test AST.
    */
   fun test(element: PsiElement): Test? = null

   /**
    * Returns a [Test] if this [LeafPsiElement] is the canonical leaf of a test AST.
    * This method will first try to determine if this leaf element is inside a test, and then
    * will invoke [test] with the container [PsiElement].
    */
   fun test(element: LeafPsiElement): Test? = null

   fun specStyleName(): String

   fun isTestElement(element: PsiElement): Boolean

   /**
    * Returns all child tests located in the given [PsiElement].
    */
   fun tests(element: PsiElement): List<TestElement> {
      return element.children.flatMap { child ->
         val childTests = tests(child)
         val test = test(child)
         if (test != null) {
            listOf(TestElement(child, test, childTests))
         } else childTests
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
