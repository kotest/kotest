package io.kotest.plugin.intellij

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.psi.enclosingClass
import io.kotest.plugin.intellij.psi.enclosingClassOrObjectForClassOrObjectToken
import io.kotest.plugin.intellij.psi.isSubclassOfSpec
import io.kotest.plugin.intellij.psi.specStyle
import io.kotest.plugin.intellij.styles.Test
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective
import com.intellij.util.Function

/**
 * Given an element, returns an [RunLineMarkerContributor.Info] if the elements line should have a gutter icon added.
 */
class KotestRunLineMarkerContributor : RunLineMarkerContributor() {

   override fun getInfo(element: PsiElement): Info? {
      // the docs say to only run a line marker for a leaf
      return when (element) {
         // ignoring white space elements will save a lot of lookups
         is PsiWhiteSpace -> null
         is LeafPsiElement -> {
//            println("getInfo for $element ${element.text} ${element.context}")
            when (element.context) {
               // rule out some common entries that can't possibly be test markers for performance
               is KtAnnotationEntry, is KtDeclarationModifierList, is KtImportDirective, is KtImportList, is KtPackageDirective -> null
               else -> markerForSpec(element) ?: markerForTest(element)
            }
         }
         else -> null
      }
   }

   private fun markerForSpec(element: LeafPsiElement): Info? {
      val ktclass = element.enclosingClassOrObjectForClassOrObjectToken() ?: return null
      return if (ktclass.isSubclassOfSpec()) icon(ktclass) else null
   }

   private fun markerForTest(element: LeafPsiElement): Info? {
      val ktclass = element.enclosingClass() ?: return null
      val style = ktclass.specStyle() ?: return null
      val test = style.test(element) ?: return null
      return icon(test)
   }

   private fun icon(ktclass: KtClassOrObject): Info {
      return Info(
         AllIcons.RunConfigurations.TestState.Run_run,
         Function<PsiElement, String> { "Run ${ktclass.fqName!!.shortName()}" },
         *ExecutorAction.getActions(1)
      )
   }

   private fun icon(test: Test): Info {
      return Info(
         AllIcons.RunConfigurations.TestState.Run,
         Function<PsiElement, String> { "Run ${test.path}" },
         *ExecutorAction.getActions(1)
      )
   }
}
