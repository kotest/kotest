package io.kotlintest.plugin.intellij.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.impl.source.tree.LeafPsiElement
import org.jetbrains.kotlin.lexer.KtToken
import org.jetbrains.kotlin.psi.KtLiteralStringTemplateEntry
import org.jetbrains.kotlin.psi.KtStringTemplateExpression

class BangIntention : PsiElementBaseIntentionAction(), IntentionAction {

  override fun getText(): String = "Add/Remove bang to test name"

  override fun getFamilyName(): String = text

  override fun isAvailable(project: Project,
                           editor: Editor?,
                           element: PsiElement): Boolean {
    return element is LeafPsiElement && element.elementType is KtToken && element.parent is KtLiteralStringTemplateEntry
  }

  override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
    val text = if (element.text.startsWith("!")) element.text.drop(1) else "!" + element.text
    (element.parent.parent as KtStringTemplateExpression).updateText(text)
  }
}