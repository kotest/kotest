package io.kotest.plugin.intellij.intentions

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement

internal var testMode = false

abstract class TestSourceOnlyIntentionAction : PsiElementBaseIntentionAction() {

   protected fun isTestSource(element: PsiElement): Boolean {
      val virtualFile: VirtualFile = element.containingFile?.virtualFile ?: return false
      return TestSourcesFilter.isTestSources(virtualFile, element.project) || testMode
   }
}
