package io.kotest.plugin.intellij

import com.intellij.openapi.editor.Editor
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiPackage
import com.intellij.testIntegration.JavaTestCreator
import com.intellij.testIntegration.createTest.CreateTestAction
import com.intellij.testIntegration.createTest.CreateTestDialog

class KotestTestCreator : JavaTestCreator() {

  private fun findElement(file: PsiFile, offset: Int): PsiElement? {
    var element = file.findElementAt(offset)
    if (element == null && offset == file.textLength) element = file.findElementAt(offset - 1)
    return element
  }

  override fun createTest(project: Project, editor: Editor, file: PsiFile) {
    val action = object : CreateTestAction() {
      override fun createTestDialog(project: Project,
                                    srcModule: Module,
                                    srcClass: PsiClass,
                                    srcPackage: PsiPackage): CreateTestDialog {
        return object : CreateTestDialog(project, text, srcClass, srcPackage, srcModule) {
          override fun suggestTestClassName(targetClass: PsiClass?): String {
            return "special test class"
          }
        }
      }
    }
    val element = findElement(file, editor.caretModel.offset)
    if (element != null && CreateTestAction.isAvailableForElement(element)) action.invoke(project, editor, element)
  }
}