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

   fun testPath(element: PsiElement): String? = null
   fun testPath2(element: LeafPsiElement): String? = null

   fun specStyleName(): String

   fun isTestElement(element: PsiElement): Boolean

   /**
    * Returns all child tests located in the given [PsiElement].
    */
   fun tests(element: PsiElement): List<TestElement> = emptyList()

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
