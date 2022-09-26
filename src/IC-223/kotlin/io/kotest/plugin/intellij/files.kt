package io.kotest.plugin.intellij

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FilenameIndex
import com.intellij.psi.search.GlobalSearchScope
import io.kotest.plugin.intellij.toolwindow.TagsFilename

fun findFiles(project: Project): List<VirtualFile> {
   return FilenameIndex
      .getVirtualFilesByName(TagsFilename, false, GlobalSearchScope.allScope(project))
      .toList()
}
