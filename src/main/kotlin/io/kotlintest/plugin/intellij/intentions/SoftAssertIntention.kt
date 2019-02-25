package io.kotlintest.plugin.intellij.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement

class SoftAssertIntention : PsiElementBaseIntentionAction(), IntentionAction {

  override fun getText(): String = "Surround statements in soft assert"

  override fun getFamilyName(): String = text

  override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
    val file = element.containingFile ?: return false
    val vFile = file.virtualFile ?: return false
    if (editor?.selectionModel?.hasSelection() == true) {
      return ProjectRootManager.getInstance(project).fileIndex.isInTestSourceContent(vFile)
    }
    return false
  }

  override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
    val factory = JavaPsiFacade.getInstance(project).elementFactory
    val softly = factory.createExpressionFromText("assertSoftly()", null)
    element.replace(softly)
  }
}