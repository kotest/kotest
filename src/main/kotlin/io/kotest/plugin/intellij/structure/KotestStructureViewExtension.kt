package io.kotest.plugin.intellij.structure

import com.intellij.icons.AllIcons
import com.intellij.ide.structureView.StructureViewExtension
import com.intellij.ide.structureView.StructureViewTreeElement
import com.intellij.ide.util.treeView.smartTree.TreeElement
import com.intellij.navigation.ItemPresentation
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbService
import com.intellij.psi.NavigatablePsiElement
import com.intellij.psi.PsiElement
import io.kotest.plugin.intellij.Test
import io.kotest.plugin.intellij.TestElement
import io.kotest.plugin.intellij.psi.specStyle
import org.jetbrains.kotlin.psi.KtClassOrObject

/**
 * This adds Kotest nodes to the Structure view in intellij (note structure view is not the project view).
 * This is used to display the tests in the structure view which are navigable.
 */
class KotestStructureViewExtension : StructureViewExtension {

   override fun getType(): Class<out PsiElement> {
      return KtClassOrObject::class.java
   }

   override fun getChildren(parent: PsiElement): Array<StructureViewTreeElement> {
      if (ApplicationManager.getApplication().isDispatchThread) {
         return emptyArray()
      }
      val ktClassOrObject = parent as? KtClassOrObject ?: return emptyArray()
      val spec = ktClassOrObject.specStyle() ?: return emptyArray()
      val tests = spec.tests(parent, false)
      return tests.map { KotestTestStructureViewTreeElement(it) }.toTypedArray()
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

   override fun getCurrentEditorElement(editor: Editor?, parent: PsiElement?): Any? {
      return null
   }
}

class KotestStructureItemPresentation(private val test: Test) : ItemPresentation {
   override fun getIcon(unused: Boolean) = AllIcons.Nodes.Test
   override fun getPresentableText(): String {
      return test.name.displayName()
   }
}
