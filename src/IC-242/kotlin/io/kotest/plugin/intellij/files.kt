package io.kotest.plugin.intellij

import com.intellij.execution.PsiLocation
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import io.kotest.plugin.intellij.psi.elementAtLine
import io.kotest.plugin.intellij.psi.isTestFile
import io.kotest.plugin.intellij.psi.toPsiLocation
import io.kotest.plugin.intellij.toolwindow.TagsFilename
import org.jetbrains.kotlin.idea.core.util.toPsiFile

fun findFiles(project: Project): List<VirtualFile> {
   return FilenameIndex
      .getVirtualFilesByName(TagsFilename, false, GlobalSearchScope.allScope(project))
      .toList()
}

fun getLocationForFile(
   project: Project,
   scope: GlobalSearchScope,
   name: String,
   lineNumber: Int
): PsiLocation<PsiElement>? {
   val testFile = FilenameIndex.getVirtualFilesByName(name, scope).firstOrNull { it.isTestFile(project) } ?: return null
   // element at line is 1 indexed, so we need to add one
   return testFile.toPsiFile(project)?.elementAtLine(lineNumber + 1)?.toPsiLocation()
}
