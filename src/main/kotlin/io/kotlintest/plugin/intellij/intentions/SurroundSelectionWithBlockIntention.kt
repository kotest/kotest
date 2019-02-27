package io.kotlintest.plugin.intellij.intentions

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtFile
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.resolve.ImportPath

abstract class SurroundSelectionWithBlockIntention : PsiElementBaseIntentionAction(), IntentionAction {

  override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
    try {
      val file = element.containingFile ?: return false
      val vFile = file.virtualFile ?: return false
      if (editor?.selectionModel?.hasSelection() == true) {
        return ProjectRootManager.getInstance(project).fileIndex.isInTestSourceContent(vFile)
      }
      return false
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
  }

  abstract val importFQN: FqName
  abstract val prefix: String

  override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
    try {

      val selection = editor?.selectionModel
      if (selection?.hasSelection() == true) {

        val file = element.containingFile
        if (file is KtFile) {

          val ktfactory = KtPsiFactory(project)

          val importPath = ImportPath(importFQN, false)
          val list = file.importList
          if (list != null) {
            if (list.imports.none { it.importPath == importPath }) {
              val imp = ktfactory.createImportDirective(importPath)
              list.add(imp)
            }
          }

          val line1 = selection.selectionStartPosition?.line ?: -1
          val linen = selection.selectionEndPosition?.line ?: -1

          if (line1 > -1 && linen > -1) {

            // if our end position is column 0, then we've selected a full line - intellij wraps this onto the next line for some reason
            val lineN0 = if (selection.selectionEndPosition?.column == 0) linen - 1 else linen

            val lineStart = editor.document.getLineStartOffset(line1)
            val lineEnd = editor.document.getLineEndOffset(lineN0)

            val text = editor.document.getText(TextRange(lineStart, lineEnd))
            // pad each of the original statements
            val prefixedStatements = text.split('\n').joinToString("\n") { "  $it" }

            val prefixWhitespace = text.takeWhile { it.isWhitespace() }

            // place the new block at the position of the original text
            val wrapped = "$prefixWhitespace$prefix {\n$prefixedStatements\n$prefixWhitespace}"

            editor.document.replaceString(lineStart, lineEnd, wrapped)
          }
        }
      }
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }
}