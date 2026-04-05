package io.kotest.plugin.intellij.breadcrumbs

import com.intellij.codeInsight.breadcrumbs.FileBreadcrumbsCollector
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.pom.PomManager
import com.intellij.pom.PomModelAspect
import com.intellij.pom.event.PomModelEvent
import com.intellij.pom.event.PomModelListener
import com.intellij.pom.tree.TreeAspect
import com.intellij.pom.tree.events.TreeChangeEvent
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.ui.components.breadcrumbs.Crumb
import com.intellij.util.containers.ContainerUtil
import io.kotest.plugin.intellij.psi.enclosingKtClassOrObject
import io.kotest.plugin.intellij.psi.kotestStyleSyntactic
import io.kotest.plugin.intellij.styles.SpecStyle
import org.jetbrains.kotlin.psi.KtClassOrObject
import org.jetbrains.kotlin.psi.KtFile
import javax.swing.Icon
import io.kotest.plugin.intellij.Icons

/**
 * Provides breadcrumbs for Kotlin files that contain Kotest spec classes.
 *
 * Breadcrumbs reflect the test tree: the outermost crumb is the spec class name,
 * followed by each enclosing test scope named by its first string argument, down to
 * the scope containing the caret.
 *
 * Test-scope detection is delegated entirely to [SpecStyle.findAssociatedTest], which
 * already knows how to walk the PSI tree for every supported spec style.
 */
class KotestBreadcrumbsCollector(private val project: Project) : FileBreadcrumbsCollector() {

   /**
    * Returns `true` only when the file is a `.kt` file containing at least one class
    * whose supertype list includes a known Kotest spec class name. The check is purely
    * syntactic (no type resolution required) and therefore safe to run on a background thread.
    */
   override fun handlesFile(virtualFile: VirtualFile): Boolean {
      if (virtualFile.extension != "kt") return false
      val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? KtFile ?: return false
      return psiFile.declarations.filterIsInstance<KtClassOrObject>().any { it.kotestStyleSyntactic() != null }
   }

   override fun watchForChanges(
      file: VirtualFile,
      editor: Editor,
      disposable: Disposable,
      changesHandler: Runnable,
   ) {
      PomManager.getModel(project).addModelListener(
         object : PomModelListener {
            override fun isAspectChangeInteresting(aspect: PomModelAspect): Boolean = aspect is TreeAspect
            override fun modelChanged(event: PomModelEvent) {
               val aspect = ContainerUtil.findInstance(event.changedAspects, TreeAspect::class.java) ?: return
               val change = event.getChangeSet(aspect) as? TreeChangeEvent ?: return
               val changedFile = change.rootElement.psi.containingFile?.virtualFile ?: return
               if (changedFile == file) changesHandler.run()
            }
         },
         disposable,
      )
   }

   override fun computeCrumbs(
      virtualFile: VirtualFile,
      document: Document,
      offset: Int,
      forcedShown: Boolean?,
   ): Iterable<Crumb> {
      val psiFile = PsiManager.getInstance(project).findFile(virtualFile) as? KtFile ?: return emptyList()
      val element = psiFile.findElementAt(offset) ?: return emptyList()
      return buildCrumbsFromElement(element)
   }

   /**
    * Builds the breadcrumb trail for [element]:
    * 1. Walks up to the enclosing [KtClassOrObject] and identifies its [SpecStyle] syntactically.
    * 2. Delegates test-scope detection to [SpecStyle.findAssociatedTest] — no reimplementation.
    * 3. Derives crumbs from [io.kotest.plugin.intellij.Test.path].
    *
    * When the caret is inside a spec class but outside any test scope (e.g. between tests),
    * only the spec class name crumb is returned.
    */
   internal fun buildCrumbsFromElement(element: PsiElement): List<Crumb> {
      val specClass: KtClassOrObject = element.enclosingKtClassOrObject() ?: return emptyList()
      val style = specClass.kotestStyleSyntactic() ?: return emptyList()
      val specCrumb = specClass.name?.let { KotestCrumb(it) } ?: return emptyList()
      val test = style.findAssociatedTest(element)
      return if (test != null) {
         listOf(specCrumb) + test.path().map { KotestCrumb(it.name) }
      } else {
         listOf(specCrumb)
      }
   }

   private class KotestCrumb(private val displayText: String) : Crumb {
      override fun getText(): String = displayText
      override fun toString(): String = displayText
      override fun getIcon(): Icon = Icons.KOTEST_16
   }
}
