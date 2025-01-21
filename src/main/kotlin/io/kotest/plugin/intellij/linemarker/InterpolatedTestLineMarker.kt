package io.kotest.plugin.intellij.linemarker

import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.icons.AllIcons
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.MainEditorLineMarkerInfo
import io.kotest.plugin.intellij.psi.enclosingKtClass
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle
import io.kotest.plugin.intellij.testMode
import org.jetbrains.kotlin.asJava.classes.KtLightClass
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtDeclarationModifierList
import org.jetbrains.kotlin.psi.KtImportDirective
import org.jetbrains.kotlin.psi.KtImportList
import org.jetbrains.kotlin.psi.KtPackageDirective

/**
 * Adds an icon to the gutter for tests which have an interpolated name.
 */
class InterpolatedTestLineMarker : LineMarkerProvider {

   private val text = "Tests with an interpolated name cannot be run using the plugin."

   // icons list https://jetbrains.design/intellij/resources/icons_list/
   private val icon = AllIcons.RunConfigurations.TestUnknown

   override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {

      // only consider tests
      if (!testMode && !ModuleUtil.hasTestSourceRoots(element.project)) return null
      if (!testMode && !element.containingFile.isTestFile()) return null

      // the docs say to only run a line marker for a leaf
      return when (element) {
         // ignoring white space elements will save a lot of lookups
         is PsiWhiteSpace -> null
         is LeafPsiElement -> {
            when (element.context) {
               // rule out some common entries that can't be individual tests for performance
               is KtAnnotationEntry, is KtDeclarationModifierList, is KtClassOrObject, is KtLightClass, is KtImportDirective, is KtImportList, is KtPackageDirective -> null
               else -> markerForTest(element)
            }
         }
         else -> null
      }
   }

   private fun markerForTest(element: LeafPsiElement): LineMarkerInfo<PsiElement>? {
      return try {
         val ktclass = element.enclosingKtClass() ?: return null
         val style = ktclass.specStyle() ?: return null
         val test = style.test(element) ?: return null
         if (test.name.interpolated) MainEditorLineMarkerInfo(element, text, icon) else null
      } catch (_: Exception) {
         null
      }
   }
}
