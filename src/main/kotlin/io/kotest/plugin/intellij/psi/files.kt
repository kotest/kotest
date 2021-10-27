package io.kotest.plugin.intellij.psi

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiFile

/**
 * Returns true if this file is located in test source.
 */
fun PsiFile.isTestFile(): Boolean {
   return ProjectRootManager.getInstance(this.project).fileIndex.isInTestSourceContent(virtualFile)
}

