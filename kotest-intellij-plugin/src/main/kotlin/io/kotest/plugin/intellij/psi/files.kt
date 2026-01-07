package io.kotest.plugin.intellij.psi

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFile

/**
 * Returns true if this [PsiFile] is located in a test source.
 */
fun PsiFile.isTestFile(): Boolean {
   return virtualFile.isTestFile(project)
}

/**
 * Returns true if this [VirtualFile] is located in a test source.
 */
fun VirtualFile.isTestFile(project: Project): Boolean {
   return TestSourcesFilter.isTestSources(this, project)
}
