package io.kotest.plugin.intellij.toolwindow

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter
import org.jetbrains.kotlin.name.FqName

const val TagsFilename = "KotestTags.kt"
val TagSuperClass = FqName("io.kotest.core.Tag")

/**
 * Whenever the special KotestTags file changes,
 * we look for tags in it and update the state. Then we
 * cause the tree model to refresh
 */
class KotestTagFileListener(
   private val tree: TestFileTree,
   project: Project,
) : PsiTreeAnyChangeAbstractAdapter() {
   private val kotestTestExplorerService: KotestTestExplorerService = project.getService(KotestTestExplorerService::class.java)

   override fun onChange(file: PsiFile?) {
      if (file == null) return
      if (file.name == TagsFilename) {
         kotestTestExplorerService.scanTags()
      }
   }
}

