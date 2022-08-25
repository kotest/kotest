package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter
import io.kotest.plugin.intellij.findFiles
import io.kotest.plugin.intellij.psi.getAllSuperClasses
import org.jetbrains.kotlin.idea.core.util.toPsiFile
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtProperty

const val TagsFilename = "KotestTags.kt"
val TagSuperClass = FqName("io.kotest.core.Tag")

/**
 * Whenever the special KotestTags file changes,
 * we look for tags in it and update the state. Then we
 * cause the tree model to refresh
 */
class KotestTagFileListener(
   private val tree: TestFileTree,
   private val project: Project,
) : PsiTreeAnyChangeAbstractAdapter() {
   override fun onChange(file: PsiFile?) {
      if (file == null) return
      if (file.name == TagsFilename) {
         scanTags(project)
         tree.reloadModel()
      }
   }
}

/**
 * Looks for Kotest tags in this file, defined at the top level as either vals or anon objects.
 */
fun PsiFile.detectKotestTags(): List<String> {
   return children.mapNotNull {
      when (it) {
         is KtClassOrObject -> if (it.getAllSuperClasses().contains(TagSuperClass)) it.name else null
         is KtProperty -> it.name
         else -> null
      }
   }
}

fun scanTags(project: Project) {
   DumbService.getInstance(project).runWhenSmart {
      TestExplorerState.tags = findFiles(project)
         .mapNotNull { it.toPsiFile(project) }
         .flatMap { it.detectKotestTags() }
         .distinct()
         .sorted()
   }
}
