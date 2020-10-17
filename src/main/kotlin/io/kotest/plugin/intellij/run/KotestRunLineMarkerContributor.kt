package io.kotest.plugin.intellij.run

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.ktclassIfCanonicalSpecLeaf
import io.kotest.plugin.intellij.psi.specStyle
import io.kotest.plugin.intellij.Test
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import com.intellij.util.Function
import io.kotest.plugin.intellij.psi.isSpec

/**
 * Given an element, returns an [RunLineMarkerContributor.Info] if the elements line should have a gutter icon added.
 */
class KotestRunLineMarkerContributor : RunLineMarkerContributor() {

   override fun getInfo(element: PsiElement): Info? {
      // the docs say to only run a line marker for a leaf
      val info = when (element) {
         // ignoring white space elements will save a lot of lookups
         is PsiWhiteSpace -> null
         is LeafPsiElement -> {
            when (element.context) {
               // rule out some common entries that can't possibly be test markers for performance
               is KtAnnotationEntry, is KtDeclarationModifierList, is KtImportDirective, is KtImportList, is KtPackageDirective -> null
               else -> markerForSpec(element) ?: markerForTest(element)
            }
         }
         else -> null
      }
      println(info)
      return info
   }

   /**
    * Returns a market for a spec if this element is the leaf element whose immediate
    * parent is the KtClass or KtObject.
    */
   private fun markerForSpec(leaf: LeafPsiElement): Info? {
      //println("Is marker for spec ${leaf.parent} ${leaf.parent::class}")
      val ktclass = leaf.ktclassIfCanonicalSpecLeaf() ?: return null
      return if (ktclass.isSpec()) icon(ktclass) else null
   }

   private fun markerForTest(element: LeafPsiElement): Info? {
      val ktclass = element.enclosingKtClass() ?: return null
      val style = ktclass.specStyle() ?: return null
      val test = style.test(element) ?: return null
      return icon(test)
   }

   private fun icon(ktclass: KtClassOrObject): Info {
      return Info(
         AllIcons.RunConfigurations.TestState.Run,
         Function<PsiElement, String> { "Run ${ktclass.fqName!!.shortName()}" },
         *ExecutorAction.getActions(1)
      )
   }

   private fun icon(test: Test): Info {
      return Info(
         AllIcons.RunConfigurations.TestState.Run,
         Function<PsiElement, String> { "Run ${test.readableTestPath()}" },
         *ExecutorAction.getActions(1)
      )
   }
}
