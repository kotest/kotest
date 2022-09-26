package io.kotest.plugin.intellij.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

/**
 * Returns true if this file is located in test source.
 */
fun PsiFile.isTestFile(): Boolean {
   return ProjectRootManager.getInstance(this.project).fileIndex.isInTestSourceContent(virtualFile)
}

fun VirtualFile.isTestFile(project: Project): Boolean {
   return ProjectRootManager.getInstance(project).fileIndex.isInTestSourceContent(this)
}
