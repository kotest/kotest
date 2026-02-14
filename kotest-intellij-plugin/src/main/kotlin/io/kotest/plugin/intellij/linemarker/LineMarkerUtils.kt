package io.kotest.plugin.intellij.linemarker

import com.intellij.diff.util.DiffUtil
import com.intellij.openapi.module.ModuleUtil
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import io.kotest.plugin.intellij.existingEditor
import io.kotest.plugin.intellij.gradle.GradleUtils
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.specStyle
import io.kotest.plugin.intellij.testMode

object LineMarkerUtils {

   fun validateElementAndReturnTestDescriptorPath(
      element: PsiElement,
      possibleLeafElements: List<String>,
      requiredKotest61OrAbove: Boolean
   ): String? {
      if (element !is LeafPsiElement) return null

      if (!testMode && !ModuleUtil.hasTestSourceRoots(element.project)) return null
      if (!testMode && !element.containingFile.isTestFile()) return null

      val editor = element.existingEditor() ?: return null
      if (DiffUtil.isDiffEditor(editor)) return null

      if (!possibleLeafElements.contains(element.elementType.toString())) return null
      if (requiredKotest61OrAbove) {
         val module = ModuleUtil.findModuleForPsiElement(element) ?: return null
         if (!GradleUtils.isKotest61OrAbove(module)) return null
      }

      val ktclass = element.enclosingKtClassOrObject() ?: return null
      val style = ktclass.specStyle() ?: return null

      val test = style.test(element) ?: return null
      if (!test.enabled) return null
      if (test.isDataTest) return null

      return test.descriptorPath()
   }
}



