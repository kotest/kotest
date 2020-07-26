package io.kotest.plugin.intellij.psi

import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.psi.PsiFile

fun PsiFile.isTestFile(): Boolean {
   val vfile = this.virtualFile
   return ProjectRootManager.getInstance(this.project).fileIndex.isInTestSourceContent(vfile)
}

