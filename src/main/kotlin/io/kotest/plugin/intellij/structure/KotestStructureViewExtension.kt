package io.kotest.plugin.intellij.structure

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewExtension
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.roots.TestSourcesFilter
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.intentions.testMode
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * This adds Kotest nodes to the Structure view in intellij (note structure view is not the project view).
 * This is used to display the tests in the structure view which are navigable.
 */
class KotestStructureViewExtension : StructureViewExtension {

   @Volatile private var cachedParent: PsiElement? = null
   @Volatile private var cachedElements: Array<StructureViewTreeElement> = emptyArray()

   override fun getType(): Class<out PsiElement> {
      return KtClassOrObject::class.java
   }

   /**
    * This method is called from background workers to populate the structure view, but also from the EDT thread
    * when the user uses navigate to next method / previous method inside the editor.
    *
    * We can't call superclasses from the EDT thread (and I cannot figure out how to make the analysis call
    * inside the superclasses method work not on the EDT thread), so we can cache the elements generated
    * when not on the EDT thread and use those.
    */
   override fun getChildren(parent: PsiElement): Array<StructureViewTreeElement> {
      require(parent is KtClassOrObject) { "Parent must be a KtClassOrObject" }

      // we need indices available in order to scan this file because in order to determine if we have
      // a spec we need to check if any of the parent classes (which are different files) are spec types
      if (DumbService.isDumb(parent.project) && !testMode) {
         return emptyArray()
      }

      val virtualFile: VirtualFile = parent.containingFile.virtualFile ?: return emptyArray()
      if (!TestSourcesFilter.isTestSources(virtualFile, parent.project) && !testMode) return emptyArray()

      // analysis doesn't work on the EDT thread
      if (ApplicationManager.getApplication().isDispatchThread) {
         // we only use the cached if we know we've already been refreshed
         return if (cachedParent == parent) {
            cachedElements
         } else {
            emptyArray()
         }
      }

      val spec = parent.specStyle() ?: return emptyArray()
      val tests = spec.tests(parent, false)
      cachedParent = parent
      cachedElements = tests.map { KotestTestStructureViewTreeElement(it) }.toTypedArray()
      return cachedElements
   }

   override fun getCurrentEditorElement(editor: Editor?, parent: PsiElement?): Any? {
      return null
   }
}

class KotestTestStructureViewTreeElement(private val testElement: TestElement) : StructureViewTreeElement {

   override fun getPresentation(): ItemPresentation = KotestStructureItemPresentation(testElement.test)

   override fun getChildren(): Array<TreeElement> {
      return testElement.nestedTests.map { KotestTestStructureViewTreeElement(it) }.toTypedArray()
   }

   override fun navigate(requestFocus: Boolean) {
      if (testElement.psi is NavigatablePsiElement) {
         testElement.psi.navigate(true)
      }
   }

   override fun canNavigate(): Boolean {
      return true
   }

   override fun canNavigateToSource(): Boolean {
      return true
   }

   override fun getValue(): Any {
      return testElement.psi
   }
}

class KotestStructureItemPresentation(private val test: Test) : ItemPresentation {
   override fun getIcon(unused: Boolean) = AllIcons.Nodes.Test
   override fun getPresentableText(): String {
      return test.name.displayName()
   }
}
